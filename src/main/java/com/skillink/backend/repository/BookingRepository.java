package com.skillink.backend.repository;

import com.skillink.backend.entity.Booking;
import com.skillink.backend.entity.User;
import com.skillink.backend.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByClient(User client);
    List<Booking> findByServiceIn(List<ServiceEntity> services);
    List<Booking> findByService(ServiceEntity service);
}
