package lifeplanner.travel.service;

import jakarta.validation.Valid;
import lifeplanner.books.model.Book;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.repository.TravelRepository;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddTripRequest;
import lifeplanner.web.dto.EditTripRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TravelService {

    private final TravelRepository travelRepository;

    @Autowired
    public TravelService(TravelRepository travelRepository) {
        this.travelRepository = travelRepository;
    }

    public List<Travel> getTripsByUser(User user) {
        return travelRepository.findAllByOwner(user);
    }

    public void addTrip(AddTripRequest addTripRequest, User user) {
        Travel travel = Travel.builder()
                .tripStatus(addTripRequest.getTripStatus())
                .tripName(addTripRequest.getTripName())
                .destination(addTripRequest.getDestination())
                .startDate(addTripRequest.getStartDate())
                .endDate(addTripRequest.getEndDate())
                .tripType(addTripRequest.getTripType())
                .accommodation(addTripRequest.getAccommodation())
                .transportation(addTripRequest.getTransportationType())
                .notes(addTripRequest.getNotes())
                .owner(user)
                .visible(false)
                .build();

        travelRepository.save(travel);
    }

    public Travel getTripById(UUID tripId) {
        return travelRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("tRIP with id [" + tripId + "] does not exist."));
    }

    public void editTrip(UUID id, EditTripRequest editTripRequest) {
        Travel trip = getTripById(id);
        trip.setTripStatus(editTripRequest.getTripStatus());
        trip.setTripName(editTripRequest.getTripName());
        trip.setDestination(editTripRequest.getDestination());
        trip.setStartDate(editTripRequest.getStartDate());
        trip.setEndDate(editTripRequest.getEndDate());
        trip.setTripType(editTripRequest.getTripType());
        trip.setAccommodation(editTripRequest.getAccommodation());
        trip.setTransportation(editTripRequest.getTransportationType());
        trip.setNotes(editTripRequest.getNotes());

        travelRepository.save(trip);
    }

    public void shareTrip(UUID tripId) {
        Travel trip = travelRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        trip.setVisible(true);
        travelRepository.save(trip);
    }

    public List<Travel> getAllTrips() {
        return travelRepository.findAll();
    }

    public List<Travel> getSharedTrips(User currentUser) {
        return travelRepository.findAllByVisibleTrue()
                .stream()
                .filter(trip -> !trip.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    public List<Travel> getMySharedTrips(User currentUser) {
        return travelRepository.findAllByVisibleTrue()
                .stream()
                .filter(trip -> trip.getOwner().getId().equals(currentUser.getId()))
                .toList();
    }

    public void removeSharing(UUID tripId) {
        Travel trip = travelRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setVisible(false);
        travelRepository.save(trip);
    }

    public void deleteTripById(UUID id) {
        travelRepository.deleteById(id);
    }

    public List<Travel> getPendingTravel() {
        return travelRepository.findAllByApprovedFalse();
    }
}
