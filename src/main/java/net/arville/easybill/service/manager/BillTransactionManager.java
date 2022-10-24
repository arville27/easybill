package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.PayBillRequest;
import net.arville.easybill.dto.response.BillTransactionResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.BillTransaction;
import net.arville.easybill.model.User;
import net.arville.easybill.model.helper.BillTransactionOrigin;

import java.math.BigDecimal;
import java.util.List;

public interface BillTransactionManager {

    BillTransactionResponse payUnpaidBills(User user, PayBillRequest payBillRequest);

    UserResponse getRelevantUsersBillTransaction(User user);

    BillTransaction createCorrespondingBillWithTransactionHeader(
            User payer,
            User receiver,
            BigDecimal payAmount,
            List<Bill> unpaidBills,
            BigDecimal totalBillToTargetUser,
            BillTransactionOrigin origin
    );

    BigDecimal getTotalOweAmountFromBills(List<Bill> unpaidBills);
}
