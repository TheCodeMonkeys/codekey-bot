const Discord = require('discord.js-commando'), fs = require('fs');
const config = JSON.parse(fs.readFileSync('config.json'));
const client = new Discord.Client({owner:'104063667351322624'});
client.login(config.token);

