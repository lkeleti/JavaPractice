package dev.lkeleti.ledgerflow.mapper;

import dev.lkeleti.ledgerflow.dto.response.AccountingConfigResponse;
import dev.lkeleti.ledgerflow.entity.AccountingConfig;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import dev.lkeleti.ledgerflow.exception.BusinessValidationException;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import org.springframework.stereotype.Component;

@Component
public class AccountingConfigMapper {

    public AccountingConfigResponse toResponse(AccountingConfig config) {

        if (config == null) {
            return null;
        }

        AccountingConfigResponse response = new AccountingConfigResponse();

        response.setId(config.getId());

        mapAccount(config.getRevenueAccount(), response, "revenue");
        mapAccount(config.getExpenseAccount(), response, "expense");
        mapAccount(config.getVatPayableAccount(), response, "vatPayable");
        mapAccount(config.getVatReceivableAccount(), response, "vatReceivable");

        return response;
    }

    private void mapAccount(GLAccount account, AccountingConfigResponse response, String type) {

        if (account == null) {
            return;
        }

        switch (type) {
            case "revenue" -> {
                response.setRevenueAccountId(account.getId());
                response.setRevenueAccountNumber(account.getNumber());
                response.setRevenueAccountName(account.getName());
            }
            case "expense" -> {
                response.setExpenseAccountId(account.getId());
                response.setExpenseAccountNumber(account.getNumber());
                response.setExpenseAccountName(account.getName());
            }
            case "vatPayable" -> {
                response.setVatPayableAccountId(account.getId());
                response.setVatPayableAccountNumber(account.getNumber());
                response.setVatPayableAccountName(account.getName());
            }
            case "vatReceivable" -> {
                response.setVatReceivableAccountId(account.getId());
                response.setVatReceivableAccountNumber(account.getNumber());
                response.setVatReceivableAccountName(account.getName());
            }
            default ->
                throw new BusinessValidationException(ErrorMessage.BUSINESS_ERROR);
        }
    }
}
