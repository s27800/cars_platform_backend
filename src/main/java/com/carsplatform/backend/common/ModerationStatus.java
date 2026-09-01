package com.carsplatform.backend.common;


/**
 * Moderation state of everything users submit.
 *
 * Reviews and fuel reports stay hidden from other users and out of the averages until they are
 * APPROVED; an approved data proposal is instead written into the car it describes.
 */
public enum ModerationStatus {
    PENDING,
    APPROVED,
    REJECTED
}
