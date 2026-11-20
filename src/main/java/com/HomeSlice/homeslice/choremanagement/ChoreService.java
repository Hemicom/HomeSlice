package com.HomeSlice.homeslice.choremanagement;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChoreService {

    private final ChoreRepository choreRepository;

    // Constructor
    public ChoreService(ChoreRepository choreRepository) {
        this.choreRepository = choreRepository;
    }

    // Add a new chore
    public Chore addChore(Chore chore) {
        return choreRepository.save(chore);
    }

    // Get all chores
    public List<Chore> getAllChores() {
        return choreRepository.findAll();
    }

    // Get a chore by ID
    public Chore getChoreById(Long id) {
        return choreRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Chore not found: " + id));
    }

    // Update an existing chore
    public Chore updateChore(Long id, Chore updatedChore) {
        Chore existingChore = getChoreById(id);

        existingChore.setName(updatedChore.getName());
        existingChore.setDescription(updatedChore.getDescription());
        existingChore.setAssignedTo(updatedChore.getAssignedTo());
        existingChore.setDueDate(updatedChore.getDueDate());
        existingChore.setCompleted(updatedChore.isCompleted());

        return choreRepository.save(existingChore);
    }

    // Delete a chore
    public void deleteChore(Long id) {
        if (!choreRepository.existsById(id)) {
            throw new RuntimeException("Chore not found: " + id);
        }

        choreRepository.deleteById(id);
    }

    // Mark a chore as completed
    public Chore completeChore(Long id) {
        Chore chore = getChoreById(id);
        chore.setCompleted(true);

        return choreRepository.save(chore);
    }

    // Get all completed chores
    public List<Chore> getCompletedChores() {
        return choreRepository.findByCompleted(true);
    }

    // Get all pending chores
    public List<Chore> getPendingChores() {
        return choreRepository.findByCompleted(false);
    }
}

