package se.digg.arbetsprov.model;

/**
 * Model-record för att representera en kund.
 */
public record Customer(long id, String name, String address, String email, String telephone) {};
