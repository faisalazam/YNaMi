package pk.lucidxpo.ynami.persistence.model.sample;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleCreationDTO;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleUpdateStatusDTO;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleUpdationDTO;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.persistence.model.sample.SampleBuilder.aSample;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;

class EntityAndDtoConversionTest {
    private final ModelMapper modelMapper = new ModelMapper();

    @Test
    void shouldConvertSampleEntityToSampleCreationDtoCorrectly() {
        Sample sample = aSample().build();

        final SampleCreationDTO convertedSampleCreationDto = modelMapper.map(sample, SampleCreationDTO.class);
        assertEquals(sample.isActive(), convertedSampleCreationDto.isActive());
        assertEquals(sample.getAddress(), convertedSampleCreationDto.getAddress());
        assertEquals(sample.getFirstName(), convertedSampleCreationDto.getFirstName());
        assertEquals(sample.getLastName(), convertedSampleCreationDto.getLastName());
    }

    @Test
    void shouldVerifyConversionsBetweenEntityAndDtos() {
        SampleCreationDTO sampleCreationDTO = SampleCreationDTO.builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(50))
                .firstName(randomAlphabetic(10))
                .lastName(randomAlphabetic(10))
                .build();

        Sample convertedSample = modelMapper.map(sampleCreationDTO, Sample.class);
        assertThat(convertedSample.isActive(), is(sampleCreationDTO.isActive()));
        assertThat(convertedSample.getAddress(), is(sampleCreationDTO.getAddress()));
        assertThat(convertedSample.getFirstName(), is(sampleCreationDTO.getFirstName()));
        assertThat(convertedSample.getLastName(), is(sampleCreationDTO.getLastName()));

        SampleUpdationDTO sampleUpdationDto = SampleUpdationDTO.builder()
                .active(chooseOneOf(true, false))
                .firstName(randomAlphabetic(10))
                .lastName(randomAlphabetic(10))
                .build();

        modelMapper.map(sampleUpdationDto, convertedSample);
        assertEquals(sampleUpdationDto.isActive(), convertedSample.isActive());
        assertEquals(sampleCreationDTO.getAddress(), convertedSample.getAddress());
        assertEquals(sampleUpdationDto.getFirstName(), convertedSample.getFirstName());
        assertEquals(sampleUpdationDto.getLastName(), convertedSample.getLastName());

        convertedSample.setActive(false);

        SampleUpdateStatusDTO sampleUpdateStatusDTO = SampleUpdateStatusDTO.builder()
                .active(true)
                .build();

        modelMapper.map(sampleUpdateStatusDTO, convertedSample);
        assertEquals(sampleUpdateStatusDTO.isActive(), convertedSample.isActive());
        assertEquals(sampleCreationDTO.getAddress(), convertedSample.getAddress());
        assertEquals(sampleUpdationDto.getFirstName(), convertedSample.getFirstName());
        assertEquals(sampleUpdationDto.getLastName(), convertedSample.getLastName());
    }
}