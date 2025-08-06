/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
export { ApiError } from './core/ApiError';
export { CancelablePromise, CancelError } from './core/CancelablePromise';
export { OpenAPI } from './core/OpenAPI';
export type { OpenAPIConfig } from './core/OpenAPI';

export type { AddParticipantRequest } from './models/AddParticipantRequest';
export type { CreateCyclistRequest } from './models/CreateCyclistRequest';
export { CreateEventRequest } from './models/CreateEventRequest';
export type { Cyclist } from './models/Cyclist';
export type { CyclistCapabilities } from './models/CyclistCapabilities';
export type { CyclistState } from './models/CyclistState';
export { Event } from './models/Event';
export type { GeoPoint } from './models/GeoPoint';
export { LeaderboardEntry } from './models/LeaderboardEntry';
export { Participant } from './models/Participant';
export type { Position } from './models/Position';
export type { Route } from './models/Route';
export type { Waypoint } from './models/Waypoint';

export { CyclistsService } from './services/CyclistsService';
export { EventsService } from './services/EventsService';
export { PositionsService } from './services/PositionsService';

// Configure API base URL
import { OpenAPI } from './core/OpenAPI';

// Use proxy for development, direct URL for production
OpenAPI.BASE = import.meta.env.PROD ? 'http://localhost:8080' : '/api';
