package com.example.ecoexchange.service;

import com.example.ecoexchange.dao.UserDAO;
import com.example.ecoexchange.dao.WithdrawalDAO;
import com.example.ecoexchange.model.Customer;
import com.example.ecoexchange.model.MembershipLevel;
import com.example.ecoexchange.model.WasteCategory;

public class TransactionService {
    private UserDAO userDAO = new UserDAO();
    private WithdrawalDAO withdrawalDAO = new WithdrawalDAO();

    private static final double LIMIT_SILVER = 20.0;
    private static final double LIMIT_GOLD = 50.0;

    public void processDeposit(Customer customer, WasteCategory wasteCategory, double weight) {
        double pricePerKg = wasteCategory.getBasePrice() + customer.getPriceBonus();
        double totalEarned = pricePerKg * weight;

        customer.addDeposit(weight, totalEarned);

        double currentTotalWeight = customer.getTotalWeightHistory();
        MembershipLevel oldLevel = customer.getMembershipLevel();

        if (currentTotalWeight >= LIMIT_GOLD) {
            customer.setMembershipLevel(MembershipLevel.GOLD);
        } else if (currentTotalWeight >= LIMIT_SILVER) {
            customer.setMembershipLevel(MembershipLevel.SILVER);
        }

        if (customer.getMembershipLevel() != oldLevel) {
            System.out.println("LEVEL UP!");
        }

        userDAO.updateCustomerStats(customer);

        System.out.println("Transaction Successful!");
        System.out.println("User: " + customer.getFullName());
        System.out.println("Level: " + customer.getMembershipLevel());
        System.out.println("Total Earned: Rp " + totalEarned);
    }

    public void processWithdrawal(Customer customer, double amount) {
        if (customer.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds! Your balance is only Rp " + customer.getBalance());
        }

        customer.deductBalance(amount);
        userDAO.updateCustomerStats(customer);
        withdrawalDAO.createWithdrawal(customer.getUserID(), amount);

        System.out.println("Withdrawal Success: Rp " + amount);
    }
}
