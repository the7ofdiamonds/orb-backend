package tech.orbfin.api.gateway.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.query.Param;
import tech.orbfin.api.gateway.entities.token.Token;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RepositoryToken extends JpaRepository<Token, Integer> {
    List<Token> findAllValidTokenByUserid(@Param("userId") Integer userId);

    Optional<Token> findByToken(String token);

    @NotNull
    @Override
    @Transactional
    <S extends Token> S saveAndFlush(@NotNull S entity);
}
