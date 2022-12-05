package net.arville.easybill.model.helper;

public enum BillStatus {
    PAID, UNPAID, ALL;

    public static BillStatus fromString(String status) {
        try {
            if (status == null) return ALL;
            return BillStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ALL;
        }
    }
}
