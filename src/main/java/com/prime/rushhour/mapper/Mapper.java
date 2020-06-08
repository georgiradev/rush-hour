package com.prime.rushhour.mapper;

import com.prime.rushhour.dto.*;
import com.prime.rushhour.entity.Activity;
import com.prime.rushhour.entity.Appointment;
import com.prime.rushhour.entity.Role;
import com.prime.rushhour.entity.User;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@org.mapstruct.Mapper(
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
    componentModel = "spring")
public interface Mapper {

  Mapper INSTANCE = Mappers.getMapper(Mapper.class);

  User userDtoToUser(UserRequestDto user);

  UserResponseDto userToUserDto(User user);

  Role roleDtoRole(RoleRequestDto roleDto);

  Appointment appointmentDtoToAppointment(AppointmentRequestDto appointment);

  @Mapping(source = "activityAppointments", target = "activities")
  @Mapping(source = "user.id", target = "userId")
  AppointmentResponseDto appointmentToAppointmentDto(Appointment appointmentDto);

  List<UserResponseDto> usersToUserDtos(List<User> users);

  @Mapping(source = "activityAppointments", target = "appointments")
  ActivityResponseDto activityToActivityDto(Activity activity);

  @Mapping(source = "user.id", target = "userId")
  AppointmentDtoWithoutActivities appointmentToAppointmentDtoWithoutActivities(
      Appointment appointment);

  @Mapping(target = "duration", source = "duration", resultType = Duration.class)
  @Mapping(target = "price", source = "price", resultType = BigDecimal.class)
  Activity activityDtoToActivity(ActivityRequestDto activityDto);

  RoleRequestDto roleToRoleDto(Role role);

  List<AppointmentResponseDto> appointmentsToAppointmentDtos(List<Appointment> appointments);

  UserResponseDtoWithoutAppointments userToUserDtoWithoutAppointments(User user);

  Appointment appointmentResponseDtoToAppointment(AppointmentResponseDto appointmentResponseDto);

  Activity ActivityDtoWithoutAppointmentsToActivity(ActivityDtoWithoutAppointments activityDto);

  ActivityDtoWithoutAppointments activityToActivityDtoWithoutAppointments(Activity activity);
}
