//package tech.orbfin.api.gateway.model.orb;
//
//import jakarta.transaction.Transactional;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.Map;
//
//@Repository
//public interface IRepositoryUserRoles  extends JpaRepository<Role, String> {
//    @Transactional
//    @Query(nativeQuery = true, value = "SELECT * FROM orb_user_roles_view")
//    public Map<String, Map<String, Boolean>> getRoles();
//}
