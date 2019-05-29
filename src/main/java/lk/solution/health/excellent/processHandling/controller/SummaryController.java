package lk.solution.health.excellent.processHandling.controller;

import lk.solution.health.excellent.common.service.DateTimeAgeService;
import lk.solution.health.excellent.general.entity.InvoiceHasLabTest;
import lk.solution.health.excellent.general.service.InvoiceHasLabTestService;
import lk.solution.health.excellent.processHandling.helpingClass.SearchProcess;
import lk.solution.health.excellent.resource.entity.MedicalPackage;
import lk.solution.health.excellent.resource.entity.Patient;
import lk.solution.health.excellent.resource.entity.User;
import lk.solution.health.excellent.resource.service.MedicalPackageService;
import lk.solution.health.excellent.resource.service.UserService;
import lk.solution.health.excellent.transaction.entity.Enum.PaymentMethod;
import lk.solution.health.excellent.transaction.entity.Invoice;
import lk.solution.health.excellent.transaction.entity.Refund;
import lk.solution.health.excellent.transaction.service.InvoiceService;
import lk.solution.health.excellent.transaction.service.RefundService;
import lk.solution.health.excellent.util.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/process")
public class SummaryController {
    private final InvoiceService invoiceService;
    private final RefundService refundService;
    private final InvoiceHasLabTestService invoiceHasLabTestService;
    private final MedicalPackageService medicalPackageService;
    private final DateTimeAgeService dateTimeAgeService;
    private final UserService userService;
    private final Operator operator;

    @Autowired
    public SummaryController(InvoiceService invoiceService, RefundService refundService, InvoiceHasLabTestService invoiceHasLabTestService, MedicalPackageService medicalPackageService, DateTimeAgeService dateTimeAgeService, UserService userService, Operator operator) {
        this.invoiceService = invoiceService;
        this.refundService = refundService;
        this.invoiceHasLabTestService = invoiceHasLabTestService;
        this.medicalPackageService = medicalPackageService;
        this.dateTimeAgeService = dateTimeAgeService;
        this.userService = userService;
        this.operator = operator;
    }


    /*    private List<BigDecimal> totalPrice = new ArrayList<>();
        private List<BigDecimal> amount = new ArrayList<>();
        private List<BigDecimal> cashAmount = new ArrayList<>();
        private List<BigDecimal> cardAmount = new ArrayList<>();
        private List<BigDecimal> refund = new ArrayList<>();
        private List<BigDecimal> medicalPackageAmount = new ArrayList<>();
        // Authentication
        private Authentication authentication;
        // to count
        private int patientCount = 0;
        private int labTestCount = 0;
        private int medicalPackgeCount = 0;
        // collection type
        private BigDecimal cashBalance;
        private BigDecimal labCollection;
        private BigDecimal discountedAmounts;
        private BigDecimal needToDeposit;
        private BigDecimal sumTotalPrice;
        private BigDecimal sumAmount;
        private BigDecimal sumCashAmount;
        private BigDecimal sumCardAmount;
        private BigDecimal sumMedicalPackage;
        private BigDecimal sumRefund;
        // to create invoice list
        private List<Invoice> invoices = new ArrayList<>();
        private List<Refund> refunds = new ArrayList<>();



        //common method to do to array clear and assign 0 to variable
        private void commonClear() {
            // set array to null
            if (!invoices.isEmpty() && !refunds.isEmpty()
                    && !totalPrice.isEmpty() && !amount.isEmpty() && !cashAmount.isEmpty() && !cardAmount.isEmpty() && !refund.isEmpty() && medicalPackageAmount.isEmpty()
                    && medicalPackgeCount != 0 && patientCount != 0) {
                // set array to null
                invoices.clear();
                refunds.clear();

                totalPrice.clear();
                amount.clear();
                cashAmount.clear();
                cardAmount.clear();
                refund.clear();
                medicalPackageAmount.clear();

                medicalPackgeCount = 0;
                patientCount = 0;
            }


            // All value set to zero
            cashBalance = BigDecimal.ZERO;
            labCollection = BigDecimal.ZERO;
            discountedAmounts = BigDecimal.ZERO;
            needToDeposit = BigDecimal.ZERO;
            sumTotalPrice = BigDecimal.ZERO;
            sumAmount = BigDecimal.ZERO;
            sumCashAmount = BigDecimal.ZERO;
            sumCardAmount = BigDecimal.ZERO;
            sumMedicalPackage = BigDecimal.ZERO;
            sumRefund = BigDecimal.ZERO;

        }

        // common method to find collection
        private void commonMethod() {

            //find total collection
            for (Invoice invoice : invoices) {

                totalPrice.add(invoice.getTotalprice());

                amount.add(invoice.getAmount());

                if (invoice.getPaymentMethod() == PaymentMethod.CASH) {
                    cashAmount.add(invoice.getAmount());
                }
                if (invoice.getPaymentMethod() == PaymentMethod.CREDITCARD) {
                    cardAmount.add(invoice.getAmount());
                }
                if (invoice.getMedicalPackage() != null) {
                    medicalPackageAmount.add(invoice.getAmount());
                    medicalPackgeCount++;
                }
            }

            //find collection on today
            for (Refund refund1 : refunds) {
                refund.add(refund1.getAmount());
            }

            for (BigDecimal totalPrices : totalPrice) {
                sumTotalPrice = sumTotalPrice.add(totalPrices);
            }
            for (BigDecimal amounts : amount) {
                sumAmount = sumAmount.add(amounts);
            }
            for (BigDecimal cashAmounts : cashAmount) {
                sumCashAmount = sumCashAmount.add(cashAmounts);
            }
            for (BigDecimal cardAmounts : cardAmount) {
                sumCardAmount = sumCardAmount.add(cardAmounts);
            }
            for (BigDecimal refunds : refund) {
                sumRefund = sumRefund.add(refunds);
            }
            for (BigDecimal medicalPackageAmounts : medicalPackageAmount) {
                sumMedicalPackage = sumMedicalPackage.add(medicalPackageAmounts);
            }
            //calculation for
            cashBalance = sumAmount.subtract(sumCardAmount);
            labCollection = sumAmount.subtract(sumMedicalPackage);
            discountedAmounts = sumTotalPrice.subtract(sumAmount);
            needToDeposit = cashBalance.subtract(sumRefund);

        }
    */
    private void commonAttributeToFontEnd(Model model, User user,
                                          List<Invoice> invoices,
                                          List<InvoiceHasLabTest> invoiceHasLabTests,
                                          List<Refund> refunds,
                                          int invoicedCount
    ) {
        BigDecimal labCollection;
        BigDecimal medicalPackageCollection = BigDecimal.ZERO;
        BigDecimal totalCollection = BigDecimal.ZERO;
        BigDecimal discountedAmount = BigDecimal.ZERO;
        BigDecimal totalCash = BigDecimal.ZERO;
        BigDecimal totalCardAndCheque;
        BigDecimal totalRefund = BigDecimal.ZERO;
        BigDecimal needToDeposit;

        HashSet<Patient> patients = new HashSet<>();
        List<Invoice> cashPaymentInvoices = new ArrayList<>();

//Medical Package included List
        List<Invoice> medicalPackageIncludeInvoice = invoices.stream()
                .filter(invoice -> invoice.getMedicalPackage() != null)
                .collect(Collectors.toList());
//Medical packages amount
        for (Invoice medicalPackageInvoice : medicalPackageIncludeInvoice)
            medicalPackageCollection = operator.addition(medicalPackageCollection, medicalPackageInvoice.getMedicalPackage().getPrice());
//Collection
        for (Invoice totalCollectionInvoice : invoices) {
            //Total Collection
            totalCollection = operator.addition(totalCollection, totalCollectionInvoice.getTotalprice());
            //Total Discount Collection
            discountedAmount = operator.addition(discountedAmount, totalCollectionInvoice.getDiscountAmount());
            //Add patient to hash set to find invoiced patient count
            patients.add(totalCollectionInvoice.getPatient());
            //if payment cash, add invoice to cash payment method
            if (totalCollectionInvoice.getPaymentMethod() == PaymentMethod.CASH) {
                cashPaymentInvoices.add(totalCollectionInvoice);
            }
        }
// lab Collection
        labCollection = operator.subtraction(totalCollection, medicalPackageCollection);
// total cash collection
        for (Invoice cashPayment : cashPaymentInvoices)
            totalCash = operator.addition(totalCash, cashPayment.getAmount());
//card and cheque collection
        totalCardAndCheque = operator.subtraction(totalCollection, operator.addition(totalCash, discountedAmount));
//total refund collection
        for (Refund refund : refunds) totalRefund = operator.addition(totalRefund, refund.getAmount());
//cash to be deposit
        needToDeposit = operator.subtraction(totalCollection, operator.addition(operator.addition(totalRefund, totalCardAndCheque), discountedAmount));


        model.addAttribute("labCollection", labCollection);
        model.addAttribute("medicalPackageCollection", medicalPackageCollection);
        model.addAttribute("totalCollection", totalCollection);
        model.addAttribute("discountedAmount", discountedAmount);
        model.addAttribute("totalCash", totalCash);
        model.addAttribute("totalCard", totalCardAndCheque);
        model.addAttribute("totalRefund", totalRefund);
        model.addAttribute("needToDeposit", needToDeposit);
        model.addAttribute("user", user.getEmployee().getName());
        model.addAttribute("search", new SearchProcess());
        model.addAttribute("print", true);
        model.addAttribute("investigationCount", invoiceHasLabTests.size());
        model.addAttribute("medicalPackageCount", medicalPackageIncludeInvoice.size());
        model.addAttribute("patientCount", patients.size());
        model.addAttribute("invoicedCount", invoicedCount);
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public String cashierSummary(Model model) {
        //get who is how is available user
        User availableUser = userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName());
        LocalDate toDay = dateTimeAgeService.getCurrentDate();

        System.out.println(toDay + " today " + toDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        List<Invoice> invoices;
        List<InvoiceHasLabTest> invoiceHasLabTests;
        List<Refund> refunds;

        int patientCount;

        if (availableUser.getRole().getName().equals("ROLE_MANAGER") || availableUser.getRole().getName().equals("ROLE_MLT1")) {
            patientCount = invoiceService.countByCreatedAt(toDay);
            invoices = invoiceService.findByDate(toDay);
            invoiceHasLabTests = invoiceHasLabTestService.findByDate(toDay);
            refunds = refundService.findByDate(toDay);


            commonAttributeToFontEnd(model, availableUser, invoices, invoiceHasLabTests, refunds, patientCount);
            model.addAttribute("search", new SearchProcess());
            model.addAttribute("date", dateTimeAgeService.getCurrentDate());
            return "/process/summary";
        }


        model.addAttribute("date", dateTimeAgeService.getCurrentDate());
        return "/process/summary";
    }
/*

    @RequestMapping(value = "/searchSummary", method = RequestMethod.POST)
    public String searchGivenDateRange(@ModelAttribute SearchProcess searchProcess,
                                       Model model, RedirectAttributes redirectAttributes) {
        LocalDateTime from = searchProcess.getStartDate();
        LocalDateTime to = searchProcess.getEndDate();
        if (from == null || to == null) {
            redirectAttributes.addFlashAttribute("alerStatu", true);
            return "redirect:/summary";
        }

        // to create invoice list
        invoices = invoiceService.findByCreatedAtIsBetween(from, to);
        refunds = refundService.findByCreatedAtIsBetween(from, to);

        // to assign count value
        patientCount = invoiceService.countByCreatedAtIsBetween(from, to);
        labTestCount = invoiceHasLabTestService.countByCreatedAtIsBetween(from, to);


        if (!invoices.isEmpty() || !refunds.isEmpty()) {
            commonClear();
            commonMethod();
        }
        model.addAttribute("labCollection", labCollection);
        model.addAttribute("medicalPackageCollection", sumMedicalPackage);
        model.addAttribute("totalCollection", sumAmount);
        model.addAttribute("discountedAmount", discountedAmounts);
        model.addAttribute("totalCash", sumCashAmount);
        model.addAttribute("totalCard", sumCardAmount);
        model.addAttribute("totalRefund", sumRefund);
        model.addAttribute("needToDeposit", needToDeposit);
        model.addAttribute("user", userService.findByUserName(authentication.getName()).getEmployee().getName());
        model.addAttribute("date", dateTimeAgeService.getCurrentDate());
        model.addAttribute("search", new SearchProcess());
        model.addAttribute("print", true);
        model.addAttribute("labTestCount", labTestCount);
        model.addAttribute("medicalPackageCount", medicalPackgeCount);
        model.addAttribute("patientCount", patientCount);
        model.addAttribute("givenDate", from + " - " + to);

        return "/process/summary";
    }
*/

}
