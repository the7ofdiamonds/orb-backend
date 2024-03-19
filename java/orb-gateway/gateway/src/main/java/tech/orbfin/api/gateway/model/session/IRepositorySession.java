//package tech.orbfin.api.gateway.model.session;
//
//import com.redis.om.spring.repository.RedisDocumentRepository;
//
//import java.util.List;
//
//public interface IRepositorySession extends RedisDocumentRepository<Session,String> {
//    List<Session> findAll();
//    Session findByAccessToken(String token);
//    Session findByRefreshToken(String refreshToken);
//    Iterable<Session> findByUsername(String username);
//    Iterable<Session> findByRevokedTrue();
//    void deleteById(String id);
//
//}