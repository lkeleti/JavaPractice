package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreatePaymentMethodCommand;
import dev.lkeleti.invotraxapp.dto.PaymentMethodDto;
import dev.lkeleti.invotraxapp.dto.UpdatePaymentMethodCommand;
import dev.lkeleti.invotraxapp.model.PaymentMethod;
import dev.lkeleti.invotraxapp.repository.PaymentMethodRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@AllArgsConstructor
@Service
public class PaymentMethodService {
    private PaymentMethodRepository paymentMethodRepository;
    private ModelMapper modelMapper;


    public List<PaymentMethodDto> getAllPaymentMethods() {
        Type targetListType = new TypeToken<List<PaymentMethodDto>>(){}.getType();
        return modelMapper.map(paymentMethodRepository.findAll(), targetListType);
    }

    public PaymentMethodDto getPaymentMethodById(Long id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find payment method")
        );
        return modelMapper.map(paymentMethod, PaymentMethodDto.class);
    }

    public PaymentMethodDto updatePaymentMethod(Long id, UpdatePaymentMethodCommand command) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find payment method")
        );
        paymentMethod.setName(command.getName());
        return modelMapper.map(paymentMethod,PaymentMethodDto.class);
    }

    public PaymentMethodDto createPaymentMethod(CreatePaymentMethodCommand command) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName(command.getName());
        return modelMapper.map(paymentMethod,PaymentMethodDto.class);
    }
}
