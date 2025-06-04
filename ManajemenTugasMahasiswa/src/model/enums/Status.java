package model.enums;

public enum Status {
    BELUM_MULAI("Belum Mulai"),
    SEDANG_DIKERJAKAN("Sedang Dikerjakan"), 
    SELESAI("Selesai");
    
    private final String displayName;
    
    Status(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    // Method untuk konversi dari string database ke enum
    public static Status fromString(String statusString) {
        if (statusString == null) {
            return BELUM_MULAI; // default value
        }
        
        // Normalisasi string (trim dan case insensitive)
        String normalized = statusString.trim();
        
        for (Status status : Status.values()) {
            if (status.displayName.equalsIgnoreCase(normalized) || 
                status.name().equalsIgnoreCase(normalized)) {
                return status;
            }
        }
        
        // Mapping untuk nilai lama yang mungkin ada di database
        switch (normalized.toLowerCase()) {
            case "belum mulai":
            case "belum_mulai":
            case "not_started":
                return BELUM_MULAI;
            case "sedang dikerjakan":
            case "sedang_dikerjakan":
            case "in_progress":
            case "progress":
                return SEDANG_DIKERJAKAN;
            case "selesai":
            case "completed":
            case "done":
                return SELESAI;
            default:
                System.err.println("Unknown status: " + statusString + ", defaulting to BELUM_MULAI");
                return BELUM_MULAI;
        }
    }
    
    // Method untuk menyimpan ke database (gunakan display name)
    public String toDatabaseString() {
        return this.displayName;
    }
}