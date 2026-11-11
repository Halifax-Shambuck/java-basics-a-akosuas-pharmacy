package com.akosua.pharmacy.app;
// Akosua's Pharmacy - stock and dispensing tracker
// Original program. Written in a hurry by the intern who has since left.
// It works, but the team is nervous about changing it. That is your job.
//
// Run it as-is first to see what it currently does before you change anything.

import com.akosua.pharmacy.model.Medication;
import com.akosua.pharmacy.model.Customer;
import com.akosua.pharmacy.service.Pharmacy;


public class PharmacyTracker {

    public static void main(String[] args) {
        Pharmacy p = new Pharmacy("Akosua's Pharmacy");

        Medication paracetamol = new Medication("Paracetamol 500mg", 12.50, 100, "OTC");
        Medication amoxicillin = new Medication("Amoxicillin 250mg", 30.00, 40, "PRESCRIPTION");
        p.addMedication(paracetamol);
        p.addMedication(amoxicillin);

        Customer kofi = new Customer("Kofi Mensah", "WALK_IN");
        Customer ama = new Customer("Ama Owusu", "REGISTERED");
        ama.phone = "0244 000 000"; // registered patients have contact details on file
        p.addCustomer(kofi);
        p.addCustomer(ama);

        // a normal morning
        p.dispense(kofi, paracetamol, 2, null);
        p.dispense(ama, amoxicillin, 1, "Dr. Boateng"); // prescription-only
        p.restock(paracetamol, 50);

        // ... and the sort of thing that keeps Akosua up at night:
        p.dispense(kofi, amoxicillin, 100, "Dr. Boateng"); // far more than we have in stock

        p.printReport();
    }
}








