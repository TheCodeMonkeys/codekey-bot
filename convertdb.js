// node.js script that converts the old csvs to jsons

const fs = require('fs');
let players_data = fs.readFileSync('data_backup.csv').split('\n');
let pdata = '';
for (let i = 0; i < players_data.length; i++) {
	let id = players_data[i].split(',')[0], exp = players_data[i].split(',')[1];
	pdata += `"${id}": { "id":${id},"exp":${exp} }} ${i < players_data.length - 1 ? ',':''}`;
}
fs.writeFileSync('players.json', `{${pdata}}`, 'utf-8');