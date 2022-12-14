package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.PayBillRequest;
import net.arville.easybill.dto.response.BillTransactionHeaderResponse;
import net.arville.easybill.dto.response.BillTransactionResponse;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.InvalidPropertiesValue;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.BillTransaction;
import net.arville.easybill.model.BillTransactionHeader;
import net.arville.easybill.model.User;
import net.arville.easybill.model.helper.BillStatus;
import net.arville.easybill.repository.BillRepository;
import net.arville.easybill.repository.BillTransactionRepository;
import net.arville.easybill.service.manager.BillTransactionManager;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillTransactionManagerImpl implements BillTransactionManager {
    private final UserManager userManager;
    private final BillTransactionRepository billTransactionRepository;
    private final BillRepository billRepository;

    @Transactional
    @Override
    public BillTransactionResponse payUnpaidBills(User user, PayBillRequest payBillRequest) {
        var missingProperties = payBillRequest.getMissingProperties();
        if (missingProperties.size() > 0) {
            throw new MissingRequiredPropertiesException(missingProperties);
        }

        var invalidProperties = new InvalidPropertiesValue();
        if (payBillRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            invalidProperties.addInvalidProperty("amount", "should be more than 0");

        BigDecimal payAmount = payBillRequest.getAmount();
        User targetUser = userManager.getUserByUserId(payBillRequest.getUserId());

        var unpaidBills = billRepository.findAllUsersBillsToSpecificUser(user.getId(), targetUser.getId());

        BigDecimal totalBillToTargetUser = unpaidBills
                .stream()
                .map(Bill::getOweAmountWithBillTransaction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalBillToTargetUser.compareTo(BigDecimal.ZERO) <= 0)
            invalidProperties.addInvalidProperty("user_id", "you do not have any unpaid bills to this user");
        else if (payBillRequest.getAmount().compareTo(totalBillToTargetUser) > 0)
            invalidProperties.addInvalidProperty("amount", "should be less or equals to " + totalBillToTargetUser);

        if (invalidProperties.isThereInvalidProperties()) throw invalidProperties;

        BillTransaction billTransaction = BillTransaction.builder()
                .payer(user)
                .receiver(targetUser)
                .amount(payAmount)
                .build();

        // Pay amount equals to total bill to target user
        if (totalBillToTargetUser.compareTo(payAmount) == 0) {
            var allPaidBills = unpaidBills
                    .stream()
                    .peek(bill -> bill.setStatus(BillStatus.PAID))
                    .map(bill -> {
                        var billTransactionHeader = BillTransactionHeader.builder()
                                .bill(bill)
                                .billTransaction(billTransaction)
                                .paidAmount(bill.getOweAmountWithBillTransaction())
                                .build();
                        bill.addBillTransactionHeader(billTransactionHeader);
                        return billTransactionHeader;
                    })
                    .collect(Collectors.toList());

            billTransaction.setBillTransactionHeaderList(allPaidBills);
        }
        // Pay amount not equals to total bill to target user, cascade to all possible bills
        else {
            final BigDecimal[] tempMaxPayableAmount = {BigDecimal.ZERO};
            var billTransactionHeaderList = unpaidBills
                    .stream()
                    .takeWhile(status -> tempMaxPayableAmount[0].compareTo(payAmount) < 0)
                    .map(status -> {
                        BigDecimal temp = payAmount.subtract(tempMaxPayableAmount[0]);

                        BigDecimal paidForThisStatus = status.getOweAmountWithBillTransaction();

                        if (paidForThisStatus.compareTo(temp) <= 0) {
                            status.setStatus(BillStatus.PAID);
                        } else {
                            paidForThisStatus = temp;
                        }

                        tempMaxPayableAmount[0] = tempMaxPayableAmount[0].add(paidForThisStatus);

                        var billTransactionHeader = BillTransactionHeader.builder()
                                .bill(status)
                                .billTransaction(billTransaction)
                                .paidAmount(paidForThisStatus)
                                .build();

                        status.addBillTransactionHeader(billTransactionHeader);

                        return billTransactionHeader;
                    })
                    .collect(Collectors.toList());

            billTransaction.setBillTransactionHeaderList(billTransactionHeaderList);
        }

        var billTransactionEntity = billTransactionRepository.save(billTransaction);

        return BillTransactionResponse.map(billTransactionEntity);
    }

    @Override
    public UserResponse getRelevantUsersBillTransaction(User user) {

        var relevantBillTransaction = billTransactionRepository.findAllRelevantTransaction(user.getId());

        return UserResponse.template(user)
                .billTransactionResponseList(relevantBillTransaction
                        .stream()
                        .map(billTransaction -> BillTransactionResponse
                                .template(billTransaction)
                                .createdAt(billTransaction.getCreatedAt())
                                .billTransactionHeaderResponseList(
                                        billTransaction.getBillTransactionHeaderList()
                                                .stream()
                                                .map(billTransactionHeader -> BillTransactionHeaderResponse
                                                        .template(billTransactionHeader)
                                                        .orderHeaderResponse(OrderHeaderResponse
                                                                .template(billTransactionHeader.getBill().getOrderHeader())
                                                                .build()
                                                        )
                                                        .build()
                                                ).collect(Collectors.toList())
                                )
                                .build()
                        )
                        .collect(Collectors.toUnmodifiableList())
                )
                .build();
    }


}
