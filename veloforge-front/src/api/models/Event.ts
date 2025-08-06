/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

import type { Participant } from './Participant';
import type { Route } from './Route';

export type Event = {
    /**
     * Unique event identifier
     */
    id: string;
    /**
     * Event name
     */
    name: string;
    /**
     * Type of cycling event
     */
    type: Event.type;
    /**
     * Current event status
     */
    status: Event.status;
    route: Route;
    participants?: Array<Participant>;
    startTime?: string;
    createdAt?: string;
};

export namespace Event {

    /**
     * Type of cycling event
     */
    export enum type {
        POINT_TO_POINT = 'point-to-point',
        LOOP = 'loop',
        BREVET = 'brevet',
    }

    /**
     * Current event status
     */
    export enum status {
        PLANNED = 'planned',
        STARTED = 'started',
        FINISHED = 'finished',
    }


}

