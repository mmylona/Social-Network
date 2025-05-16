// src/components/Timeline.js
import React from 'react';
import { Card, CardContent, Typography } from '@mui/material';
import tiStyles from './Timeline.module.css';

const Timeline = () => {
    return (
        <div className={tiStyles.timeline}>
            <Card className={tiStyles.timelineCard}>
                <CardContent>
                    <Typography variant="h5">Post Title</Typography>
                    <Typography variant="body2" color="textSecondary">This is a sample post content.</Typography>
                </CardContent>
            </Card>
        </div>
    );
};

export default Timeline;
