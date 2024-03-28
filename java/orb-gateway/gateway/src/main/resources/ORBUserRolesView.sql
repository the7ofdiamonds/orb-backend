CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`%` 
    SQL SECURITY DEFINER
VIEW `orb`.`orb_user_roles_view` AS
    SELECT 
        `orb`.`orb_options`.`option_value` AS `option_value`
    FROM
        `orb`.`orb_options`
    WHERE
        `orb`.`orb_options`.`option_name` = 'orb_user_roles'