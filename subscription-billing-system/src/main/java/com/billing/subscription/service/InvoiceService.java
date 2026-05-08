package com.billing.subscription.service;

import com.billing.subscription.dto.InvoiceDTO;
import com.billing.subscription.dto.SubscriptionDTO;
import com.billing.subscription.dto.UserDTO;
import com.billing.subscription.dto.response.PagedResponse;
import com.billing.subscription.entity.Invoice;
import com.billing.subscription.entity.Subscription;
import com.billing.subscription.entity.User;
import com.billing.subscription.exception.InvalidOperationException;
import com.billing.subscription.exception.ResourceNotFoundException;
import com.billing.subscription.repository.InvoiceRepository;
import com.billing.subscription.repository.SubscriptionRepository;
import com.billing.subscription.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        log.info("Creating new invoice for subscription ID: {}", invoiceDTO.getSubscriptionId());

        Subscription subscription = subscriptionRepository.findById(invoiceDTO.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + invoiceDTO.getSubscriptionId()));

        User user = userRepository.findById(invoiceDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + invoiceDTO.getUserId()));

        BigDecimal tax = invoiceDTO.getAmount().multiply(new BigDecimal("0.10")); // 10% tax
        BigDecimal totalAmount = invoiceDTO.getAmount().add(tax);

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + YearMonth.now() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .subscription(subscription)
                .user(user)
                .status(Invoice.InvoiceStatus.ISSUED)
                .amount(invoiceDTO.getAmount())
                .tax(tax)
                .totalAmount(totalAmount)
                .invoiceDate(invoiceDTO.getInvoiceDate() != null ? invoiceDTO.getInvoiceDate() : LocalDate.now())
                .dueDate(invoiceDTO.getDueDate() != null ? invoiceDTO.getDueDate() : LocalDate.now().plusDays(30))
                .description(invoiceDTO.getDescription())
                .notes(invoiceDTO.getNotes())
                .createdBy("SYSTEM")
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully with ID: {}", savedInvoice.getId());
        auditService.log("Invoice", savedInvoice.getId(), "CREATE", "SYSTEM", null, null);

        return mapToDTO(savedInvoice);
    }

    public InvoiceDTO getInvoiceById(Long id) {
        log.info("Fetching invoice with ID: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));
        return mapToDTO(invoice);
    }

    public PagedResponse<InvoiceDTO> getAllInvoices(Pageable pageable) {
        log.info("Fetching all invoices");
        Page<Invoice> invoices = invoiceRepository.findAll(pageable);
        return mapPageToDTO(invoices);
    }

    public PagedResponse<InvoiceDTO> searchInvoices(String search, Pageable pageable) {
        log.info("Searching invoices with keyword: {}", search);
        Page<Invoice> invoices = invoiceRepository.searchInvoices(search, pageable);
        return mapPageToDTO(invoices);
    }

    public PagedResponse<InvoiceDTO> getUserInvoices(Long userId, Pageable pageable) {
        log.info("Fetching invoices for user ID: {}", userId);
        
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Page<Invoice> invoices = invoiceRepository.findByUserId(userId, pageable);
        return mapPageToDTO(invoices);
    }

    public PagedResponse<InvoiceDTO> getSubscriptionInvoices(Long subscriptionId, Pageable pageable) {
        log.info("Fetching invoices for subscription ID: {}", subscriptionId);
        
        subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));

        Page<Invoice> invoices = invoiceRepository.findBySubscriptionId(subscriptionId, pageable);
        return mapPageToDTO(invoices);
    }

    public PagedResponse<InvoiceDTO> getInvoicesByStatus(Invoice.InvoiceStatus status, Pageable pageable) {
        log.info("Fetching invoices by status: {}", status);
        Page<Invoice> invoices = invoiceRepository.findByStatus(status, pageable);
        return mapPageToDTO(invoices);
    }

    public PagedResponse<InvoiceDTO> getOverdueInvoices(Pageable pageable) {
        log.info("Fetching overdue invoices");
        Page<Invoice> invoices = invoiceRepository.findOverdueInvoices(LocalDate.now(), pageable);
        return mapPageToDTO(invoices);
    }

    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {
        log.info("Updating invoice with ID: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        if (!invoice.getStatus().equals(Invoice.InvoiceStatus.DRAFT)) {
            throw new InvalidOperationException("Can only update draft invoices");
        }

        String oldValues = invoice.toString();

        if (invoiceDTO.getDescription() != null) {
            invoice.setDescription(invoiceDTO.getDescription());
        }
        if (invoiceDTO.getNotes() != null) {
            invoice.setNotes(invoiceDTO.getNotes());
        }
        if (invoiceDTO.getAmount() != null) {
            invoice.setAmount(invoiceDTO.getAmount());
            BigDecimal tax = invoiceDTO.getAmount().multiply(new BigDecimal("0.10"));
            invoice.setTax(tax);
            invoice.setTotalAmount(invoiceDTO.getAmount().add(tax));
        }

        invoice.setUpdatedBy("SYSTEM");
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice updated successfully with ID: {}", updatedInvoice.getId());
        auditService.log("Invoice", updatedInvoice.getId(), "UPDATE", "SYSTEM", oldValues, invoice.toString());

        return mapToDTO(updatedInvoice);
    }

    public InvoiceDTO markAsPaid(Long id) {
        log.info("Marking invoice as paid with ID: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        if (invoice.getStatus().equals(Invoice.InvoiceStatus.PAID)) {
            throw new InvalidOperationException("Invoice is already paid");
        }

        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDate.now());
        invoice.setUpdatedBy("SYSTEM");
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        
        log.info("Invoice marked as paid successfully");
        auditService.log("Invoice", id, "UPDATE", "SYSTEM", null, null);

        return mapToDTO(updatedInvoice);
    }

    public InvoiceDTO markAsOverdue(Long id) {
        log.info("Marking invoice as overdue with ID: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        invoice.setStatus(Invoice.InvoiceStatus.OVERDUE);
        invoice.setUpdatedBy("SYSTEM");
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        
        log.info("Invoice marked as overdue successfully");
        auditService.log("Invoice", id, "UPDATE", "SYSTEM", null, null);

        return mapToDTO(updatedInvoice);
    }

    public void deleteInvoice(Long id) {
        log.info("Deleting invoice with ID: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));

        if (invoice.getStatus().equals(Invoice.InvoiceStatus.PAID)) {
            throw new InvalidOperationException("Cannot delete paid invoices");
        }

        invoiceRepository.delete(invoice);
        log.info("Invoice deleted successfully with ID: {}", id);
        auditService.log("Invoice", id, "DELETE", "SYSTEM", invoice.toString(), null);
    }

    public BigDecimal getRevenueBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Getting revenue between {} and {}", startDate, endDate);
        BigDecimal revenue = invoiceRepository.getTotalRevenue(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public long getPaidInvoicesCount() {
        return invoiceRepository.countByStatus(Invoice.InvoiceStatus.PAID);
    }

    public long getOverdueInvoicesCount() {
        return invoiceRepository.countByStatus(Invoice.InvoiceStatus.OVERDUE);
    }

    private InvoiceDTO mapToDTO(Invoice invoice) {
        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .subscriptionId(invoice.getSubscription().getId())
                .userId(invoice.getUser().getId())
                .subscription(mapSubscriptionToDTO(invoice.getSubscription()))
                .user(mapUserToDTO(invoice.getUser()))
                .status(invoice.getStatus())
                .amount(invoice.getAmount())
                .tax(invoice.getTax())
                .totalAmount(invoice.getTotalAmount())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .paidDate(invoice.getPaidDate())
                .description(invoice.getDescription())
                .notes(invoice.getNotes())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }

    private SubscriptionDTO mapSubscriptionToDTO(Subscription subscription) {
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .subscriptionNumber(subscription.getSubscriptionNumber())
                .status(subscription.getStatus())
                .build();
    }

    private UserDTO mapUserToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private PagedResponse<InvoiceDTO> mapPageToDTO(Page<Invoice> page) {
        List<InvoiceDTO> content = page.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.<InvoiceDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}