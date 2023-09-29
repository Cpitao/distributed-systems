import time

import grpc

from gen import event_pb2
from gen import event_pb2_grpc

import threading

print_lock = threading.Lock()


def gamescore_to_string(score_change: event_pb2.GameScore):
    score_string = \
        score_change.footballGame.team1 + " " + \
        str(score_change.footballGame.score.team1Score) + \
        ":" + \
        str(score_change.footballGame.score.team2Score) + " " + \
        score_change.footballGame.team2
    return score_string


def read_from_stream(stream):
    for message in stream:
        with print_lock:
            print(gamescore_to_string(message))


def handle_subscription(sub):
    while True:
        try:
            stream = stub.Subscribe(sub)
            read_from_stream(stream)
        except grpc.RpcError as e:
            if e.code() == grpc.StatusCode.NOT_FOUND:
                print("No such game")
                return
            else:
                print(e)
            time.sleep(1)
        else:
            break


def parse_input():
    while True:
        user_in = input('> ')
        with print_lock:
            if user_in == "subscribe":
                sport = input("Sport (FOOTBALL - 1, TENNIS - 2): ")
                if sport not in ['1', '2']:
                    print("Invalid choice")
                    continue
                sport = int(sport)
                side1 = input("Team 1: " if sport == 1 else "Player 1: ")
                side2 = input("Team 2: " if sport == 1 else "Player 2: ")
                type = event_pb2.GameSubscription.FOOTBALL if sport == 1 else event_pb2.GameSubscription.TENNIS
                new_sub = event_pb2.GameSubscription(type=type, side1=side1, side2=side2)
                thread = threading.Thread(target=handle_subscription, args=(new_sub,))
                thread.start()
            elif user_in == "end":
                exit(0)


def main():
    parse_input()


if __name__ == "__main__":
    subscriptions = []
    retry_time = 1
    options = [
        ("grpc.http_max_pings_without_data", 0),
        ("grpc.keepalive_permit_without_calls", 1)
    ]
    channel = grpc.insecure_channel('127.0.0.5:50051', options)
    stub = event_pb2_grpc.ScoreChangeStub(channel)
    for sub in subscriptions:
        thread = threading.Thread(target=handle_subscription, args=(sub,))
        thread.start()
    retry_time = 1
    main()
