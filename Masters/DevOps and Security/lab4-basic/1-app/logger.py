"""
Application logger
"""
import logging
import os

def init_logger():
    """
    Initializes and returns the application logger
    """
    if not os.path.exists('logs'):
        os.mkdir('logs')

    logger = logging.getLogger('app_logger')
    logger.setLevel(logging.DEBUG)
    file_handler = logging.FileHandler(f'logs/app.log', mode="a")
    file_handler.setLevel(logging.DEBUG)
    file_handler.setFormatter(
        logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
    )

    logger.addHandler(file_handler)
    return logger
