package com.app.linkedinclone.model.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ExportRequest {
    private List<Long> users;
    private List<String> fields;
}
