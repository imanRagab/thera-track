package org.dci.theratrack.service;

import org.dci.theratrack.entity.Therapist;
import org.dci.theratrack.entity.User;
import org.dci.theratrack.enums.UserRole;
import org.dci.theratrack.exceptions.InvalidRequestException;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.TherapistRepository;
import org.dci.theratrack.request.TherapistRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TherapistService {
    @Autowired
    private TherapistRepository therapistRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Creates a new therapist.
     *
     * @param request the therapist to create
     * @return the created therapist
     * @throws InvalidRequestException if the therapist is null
     */
    public Therapist createTherapist(TherapistRequest request) {
        Therapist therapist = request.getTherapist();
        if (therapist == null) {
            throw new InvalidRequestException("Therapist cannot be null.");
        }
        User user = userService.createUser(request.getUser(), UserRole.THERAPIST);
        // Link the therapist to the user
        therapist.setUser(user);
        return therapistRepository.save(therapist);
    }

    public List<Therapist> getAllTherapists() {
        return therapistRepository.findAll();
    }

    /**
     * Retrieves a therapist by ID.
     *
     * @param id the ID of the therapist
     * @return the therapist
     * @throws ResourceNotFoundException if the therapist is not found
     */
    public Therapist getTherapistById(Long id) {
        return therapistRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Therapist not found with ID: " + id));
    }

    /**
     * Updates an existing therapist.
     *
     * @param id the ID of the therapist to update
     * @param updatedTherapist the updated therapist data
     * @return the updated therapist
     * @throws ResourceNotFoundException if the therapist is not found
     * @throws InvalidRequestException if the updated therapist is null
     */
    public Therapist updateTherapist(Long id, Therapist updatedTherapist) {
        if (updatedTherapist == null) {
            throw new InvalidRequestException("Updated therapist cannot be null.");
        }

        Therapist existingTherapist = therapistRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Therapist not found with ID: " + id));

        modelMapper.typeMap(Therapist.class, Therapist.class).addMappings(mapper -> mapper.skip(Therapist::setId));
        modelMapper.map(updatedTherapist, existingTherapist);

        return therapistRepository.save(existingTherapist);
    }

    /**
     * Deletes a therapist by ID.
     *
     * @param id the ID of the therapist to delete
     * @throws ResourceNotFoundException if the therapist does not exist
     */
    public void deleteTherapist(Long id) {
        if (!therapistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Therapist not found with ID: " + id);
        }
        therapistRepository.deleteById(id);
    }
}
