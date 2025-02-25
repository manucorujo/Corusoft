package com.corusoft.ticketmanager.users.repositories;

import com.corusoft.ticketmanager.tickets.entities.Ticket;
import com.corusoft.ticketmanager.users.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Long> {
    /**
     * Comprueba si existe un usuario por su nickname, ignorando mayúsculas o minúsculas
     */
    boolean existsByNicknameIgnoreCase(String nickname);

    /**
     * Recupera un usuario por su nickname, ignorando mayusculas o minúsculas
     */
    Optional<User> findByNicknameIgnoreCase(String nickname);

    /**
     * Recupera un usuario por su nombre, ignorando mayusculas o minúsculas
     */
    Optional<User> findByNameIgnoreCase(String name);

    @Query("SELECT u.tickets FROM User u WHERE u.id = ?1 ")
    List<Ticket> getSharedTickets(Long userId);

}
