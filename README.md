# config.yml
# reload with plugman
interval: 1800 # Interval in seconds (default 30 minutes)

# Math question settings
math_max_value: 50
math_operations: ["+", "-", "*", "/"]

# Word spelling challenge
words:
  - apple
  - banana
  - cherry
  - date
  - elderberry

# plugin.yml
# Custom messages
start_message: "Answer the following question to win a reward:"
correct_answer_message: "%player% answered correctly and won the reward!"
reload_message: "ChatGames configuration reloaded."
invalid_command_message: "Invalid command. Use /chatgame AnswerHere"

# Reward settings
reward_types:
  - "cloudcrate give %player% Astral 32"

name: chatgamesWhosvon
version: '${project.version}'
main: com.whosvon.chatgames.chatgames.ChatgamesWhosvon
api-version: '1.20'
load: STARTUP
commands:
  chatgame:
    description: answer the chatgame question
    usage: /chatgame <answer>
