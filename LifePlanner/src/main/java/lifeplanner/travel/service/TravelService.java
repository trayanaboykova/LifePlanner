package lifeplanner.travel.service;

import lifeplanner.exception.trips.*;
import lifeplanner.travel.model.Travel;
import lifeplanner.travel.repository.TravelRepository;
import lifeplanner.user.model.ApprovalStatus;
import lifeplanner.user.model.User;
import lifeplanner.web.dto.AddTripRequest;
import lifeplanner.web.dto.EditTripRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TravelService {

    private final TravelRepository travelRepository;
    private final TripLikesService tripLikesService;
    private final TripFavoriteService tripFavoriteService;

    @Autowired
    public TravelService(TravelRepository travelRepository, TripLikesService tripLikesService, TripFavoriteService tripFavoriteService) {
        this.travelRepository = travelRepository;
        this.tripLikesService = tripLikesService;
        this.tripFavoriteService = tripFavoriteService;
    }

    public List<Travel> getTripsByUser(User user) {
        return travelRepository.findAllByOwner(user);
    }

    @Transactional
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
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        travelRepository.save(travel);
    }

    public Travel getTripById(UUID tripId) {
        return travelRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));
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

    @Transactional
    public void shareTrip(UUID tripId) {
        Travel trip = travelRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));
        trip.setVisible(true);
        travelRepository.save(trip);
    }

    public List<Travel> getAllTrips() {
        return travelRepository.findAll();
    }

    public Map<UUID, Long> getLikeCountsForTrips(List<Travel> trips) {
        return trips.stream()
                .collect(Collectors.toMap(
                        Travel::getId,
                        trip -> tripLikesService.getLikeCount(trip.getId())
                ));
    }

    public Map<UUID, Long> getFavoriteCountsForTrips(List<Travel> trips) {
        return trips.stream()
                .collect(Collectors.toMap(
                        Travel::getId,
                        trip -> tripFavoriteService.getFavoriteCount(trip.getId())
                ));
    }

    public List<Travel> getApprovedSharedTrips(User currentUser) {
        List<Travel> approvedTrips = travelRepository.findAllByVisibleTrueAndApprovalStatus(ApprovalStatus.APPROVED);
        return approvedTrips
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

    @Transactional
    public void removeSharing(UUID tripId) {
        Travel trip = travelRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));
        trip.setVisible(false);
        travelRepository.save(trip);
    }

    @Transactional
    public void deleteTripById(UUID id) {
        travelRepository.deleteById(id);
    }

    public List<Travel> getPendingTravel() {
        return travelRepository.findAllByApprovalStatus(ApprovalStatus.PENDING);
    }

    @Transactional
    public void approveTrip(UUID tripId) {
        Travel trip = getTripById(tripId);

        if (trip.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new TripAlreadyApprovedException(tripId);
        }

        trip.setApprovalStatus(ApprovalStatus.APPROVED);
        travelRepository.save(trip);
    }

    @Transactional
    public void rejectTrip(UUID tripId) {
        Travel trip = getTripById(tripId);

        if (trip.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new TripAlreadyRejectedException(tripId);
        }

        trip.setApprovalStatus(ApprovalStatus.REJECTED);
        travelRepository.save(trip);
    }
}