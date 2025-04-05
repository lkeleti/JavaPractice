package dev.lkeleti.library.controller;

import dev.lkeleti.library.dto.CheckoutRequestDto;
import dev.lkeleti.library.dto.LoanDto;
import dev.lkeleti.library.service.LoanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
public class LoanController {
    private LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDto checkoutBook(@Valid @RequestBody CheckoutRequestDto checkoutRequestDto) {
        return loanService.checkoutBook(checkoutRequestDto);
    }

    @PutMapping("/book/{bookId}/return")
    @ResponseStatus(HttpStatus.OK)
    public LoanDto returnBook(@PathVariable Long bookId) {
        return loanService.returnBook(bookId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LoanDto> listLoans() {
        return loanService.listLoans();
    }

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public List<LoanDto> listActiveLoans() {
        return loanService.listActiveLoans();
    }

    @GetMapping("/overdue")
    @ResponseStatus(HttpStatus.OK)
    public List<LoanDto> listOverdueLoans() {
        return loanService.listOverdueLoans();
    }

    @GetMapping("/history/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public List<LoanDto> getLoanHistoryForBook(@PathVariable Long bookId) {
        return loanService.getLoanHistoryForBook( bookId);
    }
}
