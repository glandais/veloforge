/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AddParticipantRequest } from '../models/AddParticipantRequest';
import type { CreateEventRequest } from '../models/CreateEventRequest';
import type { Event } from '../models/Event';
import type { LeaderboardEntry } from '../models/LeaderboardEntry';
import type { Participant } from '../models/Participant';

import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';

export class EventsService {

    /**
     * List all events
     * @returns Event List of events
     * @throws ApiError
     */
    public static listEvents(): CancelablePromise<Array<Event>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/events',
        });
    }

    /**
     * Create a new event
     * @param requestBody
     * @returns Event Event created
     * @throws ApiError
     */
    public static createEvent(
        requestBody: CreateEventRequest,
    ): CancelablePromise<Event> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/events',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * Get event by ID
     * @param eventId
     * @returns Event Event details
     * @throws ApiError
     */
    public static getEvent(
        eventId: string,
    ): CancelablePromise<Event> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/events/{eventId}',
            path: {
                'eventId': eventId,
            },
            errors: {
                404: `Event not found`,
            },
        });
    }

    /**
     * Start an event
     * @param eventId
     * @returns Event Event started
     * @throws ApiError
     */
    public static startEvent(
        eventId: string,
    ): CancelablePromise<Event> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/events/{eventId}/start',
            path: {
                'eventId': eventId,
            },
        });
    }

    /**
     * Add cyclist to event
     * @param eventId
     * @param requestBody
     * @returns Participant Participant added
     * @throws ApiError
     */
    public static addParticipant(
        eventId: string,
        requestBody: AddParticipantRequest,
    ): CancelablePromise<Participant> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/events/{eventId}/participants',
            path: {
                'eventId': eventId,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * Get event leaderboard
     * @param eventId
     * @returns LeaderboardEntry Current leaderboard
     * @throws ApiError
     */
    public static getLeaderboard(
        eventId: string,
    ): CancelablePromise<Array<LeaderboardEntry>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/events/{eventId}/leaderboard',
            path: {
                'eventId': eventId,
            },
        });
    }

}
