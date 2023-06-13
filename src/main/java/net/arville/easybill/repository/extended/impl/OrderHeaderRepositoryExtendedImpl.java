package net.arville.easybill.repository.extended.impl;

import net.arville.easybill.model.*;
import net.arville.easybill.model.helper.BillStatus;
import net.arville.easybill.model.helper.OrderHeaderValidity;
import net.arville.easybill.repository.extended.OrderHeaderRepositoryExtended;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

public class OrderHeaderRepositoryExtendedImpl implements OrderHeaderRepositoryExtended {

    @PersistenceContext
    private EntityManager em;

    public Page<OrderHeader> findRelevantOrderHeaderForUser(
            Long userId,
            Optional<String> keyword,
            BillStatus orderStatus,
            Pageable pageable
    ) {

        CriteriaBuilder builder = em.getCriteriaBuilder();

        var query = builder.createQuery(OrderHeader.class);
        var orderHeaderRoot = query.from(OrderHeader.class);
        var orderHeaderWithBuyerRoot = orderHeaderRoot.join(OrderHeader_.buyer);
        var orderHeaderWithBillRoot = orderHeaderRoot.join(OrderHeader_.billList);
        var orderHeaderWithBillWithUserRoot = orderHeaderWithBillRoot.join(Bill_.user);
        var orderHeaderWithOrderDetailsRoot = orderHeaderRoot.join(OrderHeader_.orderDetailList);
        var orderHeaderWithOrderDetailsWithUserRoot = orderHeaderWithOrderDetailsRoot.join(OrderDetail_.user);

        var countQuery = builder.createQuery(Long.class);
        var entityCounterRoot = countQuery.from(OrderHeader.class);
        var entityCounterWithBuyerRoot = entityCounterRoot.join(OrderHeader_.buyer);
        var entityCounterWithBillRoot = entityCounterRoot.join(OrderHeader_.billList);
        var entityCounterWithBillWithUserRoot = entityCounterWithBillRoot.join(Bill_.user);
        var entityCounterWithOrderDetailsRoot = entityCounterRoot.join(OrderHeader_.orderDetailList);
        var entityCounterWithOrderDetailsWithUserRoot = entityCounterWithOrderDetailsRoot.join(OrderDetail_.user);

        List<Predicate> wherePredicates = new ArrayList<>(List.of(
                builder.equal(orderHeaderRoot.get(OrderHeader_.VALIDITY), OrderHeaderValidity.ACTIVE),
                builder.equal(orderHeaderWithOrderDetailsWithUserRoot.get(User_.ID), userId)
        ));

        List<Predicate> wherePredicatesCountQuery = new ArrayList<>(List.of(
                builder.equal(entityCounterRoot.get(OrderHeader_.VALIDITY), OrderHeaderValidity.ACTIVE),
                builder.equal(entityCounterWithOrderDetailsWithUserRoot.get(User_.ID), userId)
        ));

        keyword.ifPresent(word -> {
            wherePredicates.add(builder.or(
                            builder.like(orderHeaderWithBuyerRoot.get(User_.USERNAME), "%" + word.toLowerCase() + "%"),
                            builder.like(
                                    builder.lower(orderHeaderRoot.get(OrderHeader_.ORDER_DESCRIPTION)),
                                    "%" + word.toLowerCase() + "%"
                            )
                    )
            );

            wherePredicatesCountQuery.add(builder.or(
                    builder.like(entityCounterWithBuyerRoot.get(User_.USERNAME), "%" + word + "%"),
                    builder.like(
                            builder.lower(orderHeaderRoot.get(OrderHeader_.ORDER_DESCRIPTION)),
                            "%" + word.toLowerCase() + "%"
                    )
            ));
        });

        if (orderStatus != BillStatus.ALL) {
            wherePredicates.add(builder.and(
                    builder.equal(orderHeaderWithBillWithUserRoot.get(User_.ID), userId),
                    builder.equal(orderHeaderWithBillRoot.get(Bill_.STATUS), orderStatus)
            ));

            wherePredicatesCountQuery.add(builder.and(
                    builder.equal(entityCounterWithBillWithUserRoot.get(User_.ID), userId),
                    builder.equal(entityCounterWithBillRoot.get(Bill_.STATUS), orderStatus)
            ));
        }

        query.where(wherePredicates.toArray(Predicate[]::new));

        countQuery.where(wherePredicatesCountQuery.toArray(Predicate[]::new));

        query.orderBy(
                builder.desc(orderHeaderRoot.get(OrderHeader_.ORDER_AT)),
                builder.desc(orderHeaderRoot.get(OrderHeader_.CREATED_AT))
        );

        query.select(orderHeaderRoot).distinct(true);

        countQuery.select(builder.countDistinct(entityCounterRoot));

        var listOrderHeader = em.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        var listOrderHeaderId = listOrderHeader
                .stream()
                .map(OrderHeader::getId)
                .collect(Collectors.toSet());

        var relevantOrderDetail = this.findRelevantOrderDetail(listOrderHeaderId);
        var relevantBill = this.findRelevantBill(listOrderHeaderId);

        listOrderHeader.forEach(orderHeader -> {
            orderHeader.setOrderDetailList(
                    relevantOrderDetail.stream()
                            .filter(orderDetail -> Objects.equals(orderDetail.getOrderHeader().getId(), orderHeader.getId()))
                            .collect(Collectors.toSet())
            );
            orderHeader.setBillList(
                    relevantBill.stream()
                            .filter(bill -> Objects.equals(bill.getOrderHeader().getId(), orderHeader.getId()))
                            .collect(Collectors.toSet())
            );
        });

        var listOrderHeaderWithOthers = listOrderHeader.stream().toList();

        Long resItemCount = em.createQuery(countQuery)
                .getResultStream()
                .findFirst()
                .orElse(0L);

        return new PageImpl<>(listOrderHeaderWithOthers, pageable, resItemCount);
    }

    public Page<OrderHeader> findUsersOrderHeaderForUser(
            Long userId,
            Optional<String> keyword,
            BillStatus orderStatus,
            Pageable pageable
    ) {

        CriteriaBuilder builder = em.getCriteriaBuilder();

        var query = builder.createQuery(OrderHeader.class);
        var orderHeaderRoot = query.from(OrderHeader.class);
        var orderHeaderWithBuyerRoot = orderHeaderRoot.join(OrderHeader_.buyer);

        var countQuery = builder.createQuery(Long.class);
        var entityCounterRoot = countQuery.from(OrderHeader.class);
        var entityCounterWithBuyerRoot = entityCounterRoot.join(OrderHeader_.buyer);

        countQuery.select(builder.count(entityCounterRoot));

        List<Predicate> wherePredicates = new ArrayList<>(List.of(
                builder.equal(orderHeaderRoot.get(OrderHeader_.VALIDITY), OrderHeaderValidity.ACTIVE),
                builder.equal(orderHeaderWithBuyerRoot.get(User_.ID), userId)
        ));

        List<Predicate> wherePredicatesCountQuery = new ArrayList<>(List.of(
                builder.equal(entityCounterRoot.get(OrderHeader_.VALIDITY), OrderHeaderValidity.ACTIVE),
                builder.equal(entityCounterWithBuyerRoot.get(User_.ID), userId)
        ));

        keyword.ifPresent(word -> {
            wherePredicates.add(builder.or(
                            builder.like(orderHeaderWithBuyerRoot.get(User_.USERNAME), "%" + word.toLowerCase() + "%"),
                            builder.like(
                                    builder.lower(orderHeaderRoot.get(OrderHeader_.ORDER_DESCRIPTION)),
                                    "%" + word.toLowerCase() + "%"
                            )
                    )
            );

            wherePredicatesCountQuery.add(builder.or(
                    builder.like(entityCounterWithBuyerRoot.get(User_.USERNAME), "%" + word + "%"),
                    builder.like(
                            builder.lower(orderHeaderRoot.get(OrderHeader_.ORDER_DESCRIPTION)),
                            "%" + word.toLowerCase() + "%"
                    )
            ));
        });

        if (orderStatus != BillStatus.ALL) {
            var paidOrUnpaidOrderHeaderIdList = orderStatus == BillStatus.PAID
                    ? this.findAllPaidOrderHeaderId()
                    : this.findAllUnpaidOrderHeaderId();

            wherePredicates.add(orderHeaderRoot.get(OrderHeader_.ID).in(paidOrUnpaidOrderHeaderIdList));
            wherePredicatesCountQuery.add(orderHeaderRoot.get(OrderHeader_.ID).in(paidOrUnpaidOrderHeaderIdList));
        }

        query.where(wherePredicates.toArray(Predicate[]::new));

        countQuery.where(wherePredicatesCountQuery.toArray(Predicate[]::new));

        query.orderBy(
                builder.desc(orderHeaderRoot.get(OrderHeader_.ORDER_AT)),
                builder.desc(orderHeaderRoot.get(OrderHeader_.CREATED_AT))
        );

        query.select(orderHeaderRoot).distinct(true);

        countQuery.select(builder.countDistinct(entityCounterRoot));

        var listOrderHeader = em.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        var listOrderHeaderId = listOrderHeader
                .stream()
                .map(OrderHeader::getId)
                .collect(Collectors.toSet());

        var relevantOrderDetail = this.findRelevantOrderDetail(listOrderHeaderId);
        var relevantBill = this.findRelevantBill(listOrderHeaderId);

        listOrderHeader.forEach(orderHeader -> {
            orderHeader.setOrderDetailList(
                    relevantOrderDetail.stream()
                            .filter(orderDetail -> Objects.equals(orderDetail.getOrderHeader().getId(), orderHeader.getId()))
                            .collect(Collectors.toSet())
            );
            orderHeader.setBillList(
                    relevantBill.stream()
                            .filter(bill -> Objects.equals(bill.getOrderHeader().getId(), orderHeader.getId()))
                            .collect(Collectors.toSet())
            );
        });

        var listOrderHeaderWithOthers = listOrderHeader.stream().toList();

        Long resItemCount = em.createQuery(countQuery)
                .getResultStream()
                .findFirst()
                .orElse(0L);

        return new PageImpl<>(listOrderHeaderWithOthers, pageable, resItemCount);
    }

    private List<Long> findAllUnpaidOrderHeaderId() {
        var builder = em.getCriteriaBuilder();

        var query = builder.createQuery(Long.class);
        var orderHeaderRoot = query.from(OrderHeader.class);
        var orderHeaderWithBillRoot = orderHeaderRoot.join(OrderHeader_.billList);

        query
                .select(orderHeaderRoot.get(OrderHeader_.ID))
                .where(builder.equal(orderHeaderWithBillRoot.get(Bill_.STATUS), BillStatus.UNPAID));

        return em.createQuery(query).getResultList();
    }

    private List<Long> findAllPaidOrderHeaderId() {
        var builder = em.getCriteriaBuilder();

        var query = builder.createQuery(Long.class);
        var orderHeaderRoot = query.from(OrderHeader.class);

        var subQuery = query.subquery(Long.class);
        var subQueryRoot = subQuery.from(Bill.class);
        var subQueryWithOrderHeaderRoot = subQueryRoot.join(Bill_.orderHeader);
        subQuery
                .select(subQueryWithOrderHeaderRoot.get(OrderHeader_.ID))
                .where(
                        builder.equal(subQueryRoot.get(Bill_.STATUS), BillStatus.UNPAID),
                        builder.equal(subQueryWithOrderHeaderRoot.get(OrderHeader_.ID), orderHeaderRoot.get(OrderHeader_.ID))
                );

        query
                .select(orderHeaderRoot.get(OrderHeader_.ID))
                .where(builder.not(builder.exists(subQuery)));

        return em.createQuery(query).getResultList();
    }

    private List<OrderDetail> findRelevantOrderDetail(Set<Long> orderHeaderIdList) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        var query = builder.createQuery(OrderDetail.class);
        var orderDetailRoot = query.from(OrderDetail.class);
        var orderDetailWithOrderHeaderRoot = orderDetailRoot.join(OrderDetail_.orderHeader);

        query
                .select(orderDetailRoot).distinct(true)
                .where(orderDetailWithOrderHeaderRoot.get(OrderHeader_.ID).in(orderHeaderIdList));

        return em.createQuery(query).getResultList();
    }

    private List<Bill> findRelevantBill(Set<Long> orderHeaderIdList) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        var query = builder.createQuery(Bill.class);
        var billRoot = query.from(Bill.class);
        var billRootWithOrderHeader = billRoot.join(Bill_.orderHeader);

        query
                .select(billRoot).distinct(true)
                .where(billRootWithOrderHeader.get(OrderHeader_.ID).in(orderHeaderIdList));

        return em.createQuery(query).getResultList();
    }

}
