package model;

/** A system status row shown on the admin supervision page. */
public record SystemStatus(String label, String status, Tone tone) {
}
