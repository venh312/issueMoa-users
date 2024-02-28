package com.issuemoa.users.presentation.message;

import java.util.List;

public record ErrorMessage(List<String> errors) {
}
