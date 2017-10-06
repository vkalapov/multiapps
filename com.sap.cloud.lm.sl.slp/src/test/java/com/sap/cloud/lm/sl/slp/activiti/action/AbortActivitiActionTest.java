package com.sap.cloud.lm.sl.slp.activiti.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import com.sap.cloud.lm.sl.slp.activiti.ActivitiFacade;
import com.sap.cloud.lm.sl.slp.activiti.action.AbortActivitiAction;
import com.sap.cloud.lm.sl.slp.activiti.action.ActivitiAction;
import com.sap.lmsl.slp.SlpTaskState;

@RunWith(Parameterized.class)
public class AbortActivitiActionTest {

    private ActivitiFacade activitiFacade;
    private ActivitiAction activitiAction;
    private ActionInput input = new ActionInput();

    @Parameters
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
// @formatter:off
            // (0): With sub-process-ids and super-process-id
            {
                Arrays.asList("sub-process-1","sub-process-2","sub-process-3"), "super-process-id", "test-user-id"
            },
            // (1): With no sub-process-ids and super-process-id
            {
                Collections.emptyList(), "super-process-id", "test-user-id"
            },
            // (2): With one sub-process-id and super-process-id
            {
                Arrays.asList("sub-process-1"), "super-process-id", "test-user-id"
            },
            // (3): With sub-process-ids and no super-process-id
            {
                Arrays.asList("sub-process-1"), null, "test-user-id"
            },
// @formatter:on
        });
    }

    public AbortActivitiActionTest(List<String> activeSubprocessIds, String superProcessId, String userId) {
        this.input.activeSubprocessIds = activeSubprocessIds;
        this.input.superProcessId = superProcessId;
        this.input.userId = userId;
    }

    @Before
    public void prepareAction() {
        activitiFacade = Mockito.mock(ActivitiFacade.class);
        Mockito.when(activitiFacade.getActiveHistoricSubProcessIds(input.superProcessId)).thenReturn(input.activeSubprocessIds);
        activitiAction = new AbortActivitiAction(activitiFacade, input.userId);
    }

    @Test
    public void testExecuteAction() {
        activitiAction.executeAction(input.superProcessId);

        verifyProcessDeletion();
    }

    private void verifyProcessDeletion() {
        String expectedDeleteReason = SlpTaskState.SLP_TASK_STATE_ABORTED.value();
        for (String subProcessId : input.activeSubprocessIds) {
            Mockito.verify(activitiFacade).deleteProcessInstance(input.userId, subProcessId, expectedDeleteReason);
        }
        Mockito.verify(activitiFacade).deleteProcessInstance(input.userId, input.superProcessId, expectedDeleteReason);
    }

    private static class ActionInput {
        List<String> activeSubprocessIds;
        String superProcessId;
        String userId;
    }
}
