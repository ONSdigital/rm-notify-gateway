package uk.gov.ons.ctp.response.notify.util;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import uk.gov.ons.ctp.response.notify.domain.Response;
import uk.gov.ons.ctp.response.notify.lib.notify.ResponseDTO;

@Mapper
public interface ResponseMapper {
  
  ResponseMapper INSTANCE = Mappers.getMapper(ResponseMapper.class);

  ResponseDTO mapToResponseDto(Response response);
}