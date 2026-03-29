UPDATE users 
SET role = 'DOCTOR' 
WHERE role = 'ROLE_DOCTOR';

UPDATE users 
SET role = 'PATIENT' 
WHERE role = 'ROLE_PATIENT';

UPDATE users 
SET role = 'ADMIN' 
WHERE role = 'ROLE_ADMIN'; 