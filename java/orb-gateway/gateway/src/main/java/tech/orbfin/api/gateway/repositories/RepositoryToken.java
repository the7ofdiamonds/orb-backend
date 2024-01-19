package tech.orbfin.api.gateway.repositories;

import org.springframework.data.repository.query.Param;
import tech.orbfin.api.gateway.token.Token;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface RepositoryToken extends JpaRepository<Token, Integer> {
    @Query(value = """
            SELECT t FROM Token t
            JOIN t.user u
            WHERE u.id = :userId AND (t.expired = false OR t.revoked = false)
            """)
    List<Token> findAllValidTokenByUser(@Param("userId") Integer userId);

    Optional<Token> findByToken(String token);

    @Query(value = "INSERT INTO Token (token) VALUES (:token)", nativeQuery = true)
    void saveToken(@Param("token") String token);
}

