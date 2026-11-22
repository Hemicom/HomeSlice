package com.HomeSlice.homeslice.choremanagement;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/chores")
public class ChoreController {

    private final ChoreService choreService;

    // Constructor
    public ChoreController(ChoreService choreService) {
        this.choreService = choreService;
    }

    // Get all chores
    @GetMapping
    public List<Chore> getAllChores() {
        return choreService.getAllChores();
    }

    // Get a specific chore
    @GetMapping("/{id}")
    public Chore getChoreById(@PathVariable Long id) {
        return choreService.getChoreById(id);
    }

    // Create a new chore
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Chore addChore(@RequestBody Chore chore) {
        return choreService.addChore(chore);
    }

    // Update an existing chore
    @PutMapping("/{id}")
    public Chore updateChore(
            @PathVariable Long id,
            @RequestBody Chore chore) {
        return choreService.updateChore(id, chore);
    }

    // Delete a chore
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChore(@PathVariable Long id) {
        choreService.deleteChore(id);
        return ResponseEntity.noContent().build();
    }

    // Mark a chore as completed
    @PutMapping("/{id}/complete")
    public Chore completeChore(@PathVariable Long id) {
        return choreService.completeChore(id);
    }

    // Get completed chores
    @GetMapping("/completed")
    public List<Chore> getCompletedChores() {
        return choreService.getCompletedChores();
    }

    // Get pending chores
    @GetMapping("/pending")
    public List<Chore> getPendingChores() {
        return choreService.getPendingChores();
    }
}

