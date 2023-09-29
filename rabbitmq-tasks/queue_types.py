from enum import Enum


class QueueType(Enum):
    PEOPLE_TRANSPORT = 1
    CARGO_TRANSPORT = 2
    SATELLITE_TRANSPORT = 3

    def __str__(self):
        if self.value == 1:
            return "PEOPLE_TRANSPORT"
        elif self.value == 2:
            return "CARGO_TRANSPORT"
        elif self.value == 3:
            return "SATELLITE_TRANSPORT"
