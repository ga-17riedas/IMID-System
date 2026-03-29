package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.*;
import com.example.medicalsystem.model.*;
import com.example.medicalsystem.repository.*;
import com.example.medicalsystem.service.AdminUserService;
import com.example.medicalsystem.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.Random;
import java.time.LocalDateTime;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final Logger log = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<DoctorDTO> getDoctors(Pageable pageable, String search, String department) {
        Page<Doctor> doctors;
        if (search != null && department != null) {
            doctors = doctorRepository.findByFullNameContainingAndDepartment(search, department, pageable);
        } else if (search != null) {
            doctors = doctorRepository.findByFullNameContaining(search, pageable);
        } else if (department != null) {
            doctors = doctorRepository.findByDepartment(department, pageable);
        } else {
            doctors = doctorRepository.findAll(pageable);
        }
        return doctors.map(this::convertToDoctorDTO);
    }

    @Override
    @Transactional
    public DoctorDTO createDoctor(DoctorCreateRequest request) {
        // 创建用户账号
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("DOCTOR");
        userRepository.save(user);

        // 创建医生信息
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setFullName(request.getFullName());
        doctor.setProfessionalTitle(request.getProfessionalTitle());
        doctor.setDepartment(request.getDepartment());
        doctor.setHospital(request.getHospital());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setSpecialty(request.getSpecialty());
        
        return convertToDoctorDTO(doctorRepository.save(doctor));
    }

    @Override
    public Page<PatientDTO> getPatients(Pageable pageable, String search, String gender) {
        Page<Patient> patients;
        
        if (search != null && gender != null) {
            // 处理性别参数，确保大小写一致
            log.debug("查询患者：姓名={}, 性别={}", search, gender);
            patients = patientRepository.findByFullNameContainingAndGender(search, gender, pageable);
        } else if (search != null) {
            log.debug("查询患者：姓名={}", search);
            patients = patientRepository.findByFullNameContaining(search, pageable);
        } else if (gender != null) {
            // 处理性别参数，确保大小写一致
            log.debug("查询患者：性别={}", gender);
            patients = patientRepository.findByGender(gender, pageable);
        } else {
            log.debug("查询所有患者");
            patients = patientRepository.findAll(pageable);
        }
        return patients.map(this::convertToPatientDTO);
    }

    @Override
    public Page<MedicalHistoryDTO> getPatientHistory(Long patientId, Pageable pageable) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));
                
        Page<MedicalHistory> histories = medicalHistoryRepository.findByPatient(patient, pageable);
        return histories.map(this::convertToMedicalHistoryDTO);
    }

    @Override
    @Transactional
    public DoctorDTO updateDoctor(Long id, DoctorUpdateRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        
        doctor.setFullName(request.getFullName());
        doctor.setProfessionalTitle(request.getProfessionalTitle());
        doctor.setDepartment(request.getDepartment());
        doctor.setHospital(request.getHospital());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setSpecialty(request.getSpecialty());
        
        User user = doctor.getUser();
        user.setEmail(request.getEmail());
        userRepository.save(user);
        
        return convertToDoctorDTO(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        
        userRepository.delete(doctor.getUser());
        doctorRepository.delete(doctor);
    }

    @Override
    public Page<AdminDTO> getAdmins(Pageable pageable, String search) {
        Page<User> admins;
        if (search != null) {
            admins = userRepository.findByRoleAndUsernameContainingOrEmailContaining("ADMIN", search, search, pageable);
        } else {
            admins = userRepository.findByRole("ADMIN", pageable);
        }
        return admins.map(this::convertToAdminDTO);
    }

    @Override
    @Transactional
    public AdminDTO createAdmin(AdminCreateRequest request) {
        // 验证用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("ROLE_ADMIN");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);
        return convertToAdminDTO(user);
    }

    @Override
    @Transactional
    public AdminDTO updateAdminStatus(Long id, String status) {
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));
        
        admin.setStatus(status);
        return convertToAdminDTO(userRepository.save(admin));
    }

    @Override
    @Transactional
    public PasswordResetResponse resetAdminPassword(Long id) {
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));
        
        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        admin.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(admin);
        
        return new PasswordResetResponse(newPassword);
    }

    @Override
    @Transactional
    public PatientDTO updatePatient(Long id, PatientUpdateRequest request) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("患者", "id", id));
        
        // 更新患者信息
        patient.setFullName(request.getFullName());
        
        // 规范化性别字段
        String gender = request.getGender();
        if (gender != null) {
            gender = gender.toUpperCase();
            if (!gender.equals("MALE") && !gender.equals("FEMALE")) {
                // 如果不是标准格式，进行映射
                if (gender.startsWith("M") || gender.equals("男") || gender.equals("1")) {
                    gender = "MALE";
                } else if (gender.startsWith("F") || gender.equals("女") || gender.equals("2")) {
                    gender = "FEMALE";
                }
            }
            patient.setGender(gender);
        }
        
        patient.setAge(request.getAge());
        patient.setPhone(request.getPhone());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setEmergencyPhone(request.getEmergencyPhone());
        
        // 更新用户邮箱
        if (request.getEmail() != null) {
            User user = patient.getUser();
            user.setEmail(request.getEmail());
            userRepository.save(user);
        }
        
        Patient updatedPatient = patientRepository.save(patient);
        return convertToPatientDTO(updatedPatient);
    }

    @Override
    public PasswordResetResponse resetDoctorPassword(Long id) {
        User user = doctorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("医生", "id", id))
            .getUser();
        
        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return new PasswordResetResponse(newPassword);
    }

    @Override
    public PasswordResetResponse resetPatientPassword(Long id) {
        User user = patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("患者", "id", id))
            .getUser();
        
        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return new PasswordResetResponse(newPassword);
    }

    private String generateRandomPassword() {
        // 生成8位随机密码
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private DoctorDTO convertToDoctorDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setUsername(doctor.getUser().getUsername());
        dto.setFullName(doctor.getFullName());
        dto.setProfessionalTitle(doctor.getProfessionalTitle());
        dto.setDepartment(doctor.getDepartment());
        dto.setHospital(doctor.getHospital());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setEmail(doctor.getUser().getEmail());
        return dto;
    }

    private PatientDTO convertToPatientDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setUsername(patient.getUser().getUsername());
        dto.setFullName(patient.getFullName());
        
        // 确保性别字段正确转换
        String gender = patient.getGender();
        if (gender != null) {
            // 统一转换为大写存储，保证一致性
            gender = gender.toUpperCase();
            if (!gender.equals("MALE") && !gender.equals("FEMALE")) {
                // 如果不是标准格式，进行映射
                if (gender.startsWith("M") || gender.equals("男") || gender.equals("1")) {
                    gender = "MALE";
                } else if (gender.startsWith("F") || gender.equals("女") || gender.equals("2")) {
                    gender = "FEMALE";
                }
            }
            dto.setGender(gender);
        }
        
        dto.setAge(patient.getAge());
        dto.setPhone(patient.getPhone());
        dto.setAddress(patient.getAddress());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setEmergencyPhone(patient.getEmergencyPhone());
        dto.setBirthDate(patient.getBirthDate());
        dto.setEmail(patient.getUser().getEmail());
        return dto;
    }

    private MedicalHistoryDTO convertToMedicalHistoryDTO(MedicalHistory history) {
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        dto.setId(history.getId());
        dto.setDiagnosis(history.getDiagnosis());
        dto.setTreatment(history.getTreatment());
        dto.setVisitDate(history.getVisitDate());
        dto.setDoctorName(history.getDoctor().getFullName());
        dto.setDepartment(history.getDoctor().getDepartment());
        return dto;
    }

    private AdminDTO convertToAdminDTO(User user) {
        AdminDTO dto = new AdminDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        
        if (user.getCreatedAt() != null) {
            dto.setCreatedAt(user.getCreatedAt());
        } else {
            dto.setCreatedAt(LocalDateTime.now());
        }
        
        if (user.getLastLogin() != null) {
            dto.setLastLogin(user.getLastLogin());
        }
        
        return dto;
    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setUsername(patient.getUser().getUsername());
        dto.setFullName(patient.getFullName());
        dto.setGender(patient.getGender());
        dto.setAge(patient.getAge());
        dto.setPhone(patient.getPhone());
        dto.setEmail(patient.getEmail());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setEmergencyPhone(patient.getEmergencyPhone());
        return dto;
    }
} 