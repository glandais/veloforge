/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

export type Participant = {
    /**
     * Reference to cyclist
     */
    cyclistId: string;
    /**
     * Current participation status
     */
    status: Participant.status;
    startTime?: string;
    finishTime?: string;
};

export namespace Participant {

    /**
     * Current participation status
     */
    export enum status {
        REGISTERED = 'registered',
        STARTED = 'started',
        RIDING = 'riding',
        RESTING = 'resting',
        FINISHED = 'finished',
        DNF = 'dnf',
    }


}

