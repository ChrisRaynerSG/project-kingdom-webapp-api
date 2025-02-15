package com.carnasa.cr.projectkingdomwebpage.exceptions;

public record ErrorResponse(Object errorDetails, String message, String url) {
}
