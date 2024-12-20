package org.dci.theratrack.controller;

import jakarta.validation.Valid;
import org.dci.theratrack.entity.Therapist;
import org.dci.theratrack.request.TherapistRequest;
import org.dci.theratrack.service.TherapistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/therapists")
public class TherapistController {

    @Autowired
    private TherapistService therapistService;

    @PostMapping
    public ResponseEntity<Therapist> createTherapist(@RequestBody @Valid TherapistRequest request) {
        Therapist createdTherapist = therapistService.createTherapist(request);
        return ResponseEntity.ok(createdTherapist);
    }

    @GetMapping
    public List<Therapist> getAllTherapists() {
        return therapistService.getAllTherapists();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Therapist> getTherapistById(@PathVariable Long id) {
        Therapist therapist = therapistService.getTherapistById(id);
        return ResponseEntity.ok(therapist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Therapist> updateTherapist(@PathVariable Long id, @RequestBody @Valid Therapist therapist) {
        return ResponseEntity.ok(therapistService.updateTherapist(id, therapist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTherapist(@PathVariable Long id) {
        therapistService.deleteTherapist(id);
        return ResponseEntity.noContent().build();
    }
}
