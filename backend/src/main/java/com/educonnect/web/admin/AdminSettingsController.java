package com.educonnect.web.admin;

import com.educonnect.application.admin.dto.HolidayDto;
import com.educonnect.application.admin.dto.SystemSettingDto;
import com.educonnect.domain.Holiday;
import com.educonnect.domain.SystemSetting;
import com.educonnect.repository.HolidayRepository;
import com.educonnect.repository.SystemSettingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final HolidayRepository holidayRepository;
    private final SystemSettingRepository systemSettingRepository;

    // --- Holidays ---
    @GetMapping("/holidays")
    public List<HolidayDto> listHolidays(
            @RequestParam(required = false) Integer year) {
        List<Holiday> list = year != null
                ? holidayRepository.findByHolidayDateYear(year)
                : holidayRepository.findAll();
        return list.stream().map(this::toHolidayDto).collect(Collectors.toList());
    }

    @PostMapping("/holidays")
    public ResponseEntity<HolidayDto> createHoliday(@RequestBody HolidayDto req) {
        Holiday h = Holiday.builder()
                .holidayDate(req.getHolidayDate())
                .name(req.getName())
                .description(req.getDescription())
                .build();
        h = holidayRepository.save(h);
        return ResponseEntity.status(HttpStatus.CREATED).body(toHolidayDto(h));
    }

    @PutMapping("/holidays/{id}")
    public ResponseEntity<HolidayDto> updateHoliday(@PathVariable String id, @RequestBody HolidayDto req) {
        return holidayRepository.findById(id)
                .map(h -> {
                    h.setHolidayDate(req.getHolidayDate());
                    h.setName(req.getName());
                    h.setDescription(req.getDescription());
                    holidayRepository.save(h);
                    return ResponseEntity.ok(toHolidayDto(h));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable String id) {
        if (holidayRepository.existsById(id)) {
            holidayRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- Key-value settings ---
    @GetMapping("/keys")
    public List<SystemSettingDto> listSettings() {
        return systemSettingRepository.findAll().stream().map(this::toSettingDto).collect(Collectors.toList());
    }

    @GetMapping("/keys/{key}")
    public ResponseEntity<SystemSettingDto> getSetting(@PathVariable String key) {
        return systemSettingRepository.findByKeyName(key)
                .map(s -> ResponseEntity.ok(toSettingDto(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/keys/{key}")
    public ResponseEntity<SystemSettingDto> setSetting(
            @PathVariable String key,
            @RequestBody java.util.Map<String, String> body) {
        String value = body != null ? body.get("value") : null;
        String description = body != null ? body.get("description") : null;
        SystemSetting s = systemSettingRepository.findByKeyName(key).orElse(null);
        if (s == null) {
            s = SystemSetting.builder().keyName(key).value(value != null ? value : "").description(description).build();
        } else {
            if (value != null) s.setValue(value);
            if (description != null) s.setDescription(description);
        }
        s = systemSettingRepository.save(s);
        return ResponseEntity.ok(toSettingDto(s));
    }

    private HolidayDto toHolidayDto(Holiday h) {
        HolidayDto dto = new HolidayDto();
        dto.setId(h.getId());
        dto.setHolidayDate(h.getHolidayDate());
        dto.setName(h.getName());
        dto.setDescription(h.getDescription());
        return dto;
    }

    private SystemSettingDto toSettingDto(SystemSetting s) {
        SystemSettingDto dto = new SystemSettingDto();
        dto.setId(s.getId());
        dto.setKeyName(s.getKeyName());
        dto.setValue(s.getValue());
        dto.setDescription(s.getDescription());
        return dto;
    }
}
