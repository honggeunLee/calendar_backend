package org.example.calendar_backend.repository;

import org.example.calendar_backend.entity.Friendship;
import org.example.calendar_backend.entity.FriendshipStatus;
import org.example.calendar_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findByUserAndStatus(User user, FriendshipStatus status);

    Optional<Friendship> findByUserAndFriend(User user, User friend);
}
