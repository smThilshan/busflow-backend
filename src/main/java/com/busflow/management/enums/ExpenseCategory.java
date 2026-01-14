package com.busflow.management.enums;

public enum ExpenseCategory {
    FUEL("Fuel"),
    MAINTENANCE_REPAIR("Maintenance & Repair"),
    OIL_LUBRICANTS_BRAKE("Oil/ Lubricants / Brake Pads"),
    ELECTRICAL_WIRING_LIGHTS("Electrical / Wiring / Lights"),
    TYRES_SPARES("Tyres & Spares"),
    CLEANING_WASHING("Cleaning / Washing"),
    PERMIT_LICENSE_INSURANCE("Permit / License / Insurance"),
    COMMISSION("Commission"),
    FINE_PENALTY("Fine / Penalty"),
    BODY_PAINTING("Body / Painting"),
    OTHER("Other");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
