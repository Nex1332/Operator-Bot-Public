package de.operator.bot.service.impl;

import de.operator.bot.entity.Operator;
import de.operator.bot.repository.OperatorRepository;
import de.operator.bot.service.OperatorsInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorsInfoServiceImpl implements OperatorsInfoService {
    private final OperatorRepository operatorRepository;

    @Override
    public List<String> getAllOperatorsUserName() {
        List<Operator> operators = operatorRepository.findAll();

        return operators.stream()
                .map(operator -> "@" + operator.getUsername())
                .collect(Collectors.toList());
    }
}
