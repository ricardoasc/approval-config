package io.sicredi.error;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum  ErrorDefinitionImpl implements ErrorDefinition {
    IAC_CONFIG_NOT_FOUND_ERROR_MSG("IAC-01", "Configuração não encontrada."),
    IAC_PRODUCT_PARAMETERS_NOT_FOUND_MSG("IAC-02", "Não existe parametrizações para o produto informado."),
    IAC_INVALID_MAXIMUM_PROFITABILITY_MSG("IAC-03", "Range de rentabilidade não é valido, revise a taxa extra balcão."),
    IAC_CONFIG_DUPLICATED_MSG("IAC-04", "Esta configuração existe e está vigente, por isso não pode ser duplicada."),
    IAC_TASK_INVALID_MIN_MAX_RATE_MSG("IAC-05", "A taxa máxima da tarefa deve ser maior ou igual a última."),
    IAC_TASK_NOT_FOUND_MSG("IAC-06", "Tarefa não encontrada para esta configuração."),
    IAC_TASK_INVALID_MAX_RATE_MSG("IAC-07", "Tarefa inválida, maxRatePercent não pode ser menor que minRatePercent."),
    IAC_CONFIG_TASK_INVALID_RATE_MSG("IAC-08", "Ao menos uma tarefa precisa ter a taxa máxima permitida para o produto."),
    IAC_TASK_WITH_DUPLICATED_USER("IAC-09", "Esta configuração possui tarefa com usuário duplicado.");

    private final String code;
    private final String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
