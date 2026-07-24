package com.agentseek.api.controller;

import com.agentseek.common.result.Result;
import com.agentseek.core.model.Workflow;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工作流管理 REST API
 */
@RestController
@RequestMapping("/api/v1/workflows")
public class WorkflowController {

    private final Map<String, Workflow> workflowStore = new ConcurrentHashMap<>();

    @PostMapping
    public Result<String> createWorkflow(@RequestBody Workflow workflow) {
        workflowStore.put(workflow.getWorkflowId(), workflow);
        return Result.ok(workflow.getWorkflowId());
    }

    @GetMapping("/{workflowId}")
    public Result<Workflow> getWorkflow(@PathVariable String workflowId) {
        return Result.ok(workflowStore.get(workflowId));
    }

    @GetMapping
    public Result<List<Workflow>> listWorkflows() {
        return Result.ok(new ArrayList<>(workflowStore.values()));
    }

    @DeleteMapping("/{workflowId}")
    public Result<Void> deleteWorkflow(@PathVariable String workflowId) {
        workflowStore.remove(workflowId);
        return Result.ok();
    }
}
