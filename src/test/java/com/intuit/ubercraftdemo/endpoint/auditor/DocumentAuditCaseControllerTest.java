package com.intuit.ubercraftdemo.endpoint.auditor;

import com.intuit.ubercraftdemo.exception.advice.RestExceptionHandler;
import com.intuit.ubercraftdemo.model.AuditorOnboardingStep;
import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import com.intuit.ubercraftdemo.model.StepStatus;
import com.intuit.ubercraftdemo.model.repository.AuditorOnboardingStepRepository;
import com.intuit.ubercraftdemo.model.repository.BudgetEditionS3Repository;
import com.intuit.ubercraftdemo.model.repository.DriverOnboardingProcessRepository;
import com.intuit.ubercraftdemo.model.repository.DriverOnboardingStepRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DocumentAuditCaseController.class)
public class DocumentAuditCaseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DriverOnboardingStepRepository driverOnboardingStepRepository;
    @MockBean
    private AuditorOnboardingStepRepository auditorOnboardingStepRepository;
    @MockBean
    private DriverOnboardingProcessRepository driverOnboardingProcessRepository;
    @MockBean
    private BudgetEditionS3Repository budgetEditionS3Repository;
    @MockBean
    private RestExceptionHandler restExceptionHandler;

    @Test
    public void whenNoCaseFound_thenReturnsNothingLeftToDo() throws Exception {
        when(auditorOnboardingStepRepository.findAllByUsername("kyc-auditor@gmail.com"))
            .thenReturn(Arrays.asList(new AuditorOnboardingStep()));
        when(driverOnboardingStepRepository.findOldestDriverOnboardingStep(Arrays.asList(), StepStatus.WaitingForAuditorAssignment))
            .thenReturn(Optional.empty());
        mockMvc.perform(patch("/auditor/document-verification-case/assign")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Nothing left to do."));
    }
    @Test
    public void whenCaseFound_thenPatchAndReturnCaseAssigned() throws Exception {
        AuditorOnboardingStep auditorOnboardingStep = new AuditorOnboardingStep();
        DriverOnboardingStep driverOnboardingStep = new DriverOnboardingStep();
        driverOnboardingStep.setId(1);
        driverOnboardingStep.setStatus(StepStatus.WaitingForAuditorAssignment);
        when(auditorOnboardingStepRepository.findAllByUsername("kyc-auditor@gmail.com"))
            .thenReturn(Arrays.asList(auditorOnboardingStep));
        when(driverOnboardingStepRepository.findOldestDriverOnboardingStep(Arrays.asList(), StepStatus.WaitingForAuditorAssignment))
            .thenReturn(Optional.of(driverOnboardingStep));
        mockMvc.perform(patch("/auditor/document-verification-case/assign")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("You have been assigned this case " + driverOnboardingStep.getId()));
    }
}