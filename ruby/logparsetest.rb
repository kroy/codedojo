#an experiment in parsing a specific log format
# @TODO : add xp snapshots
# 		handle player disconnects
# 		place all events on map

class Hero
	def initialize(slot, nickname)
		@slot = slot
		@nickname = nickname
		@gold = 0
		@kills = []
		@deaths = []
		@assists = []
		@goldsnapshots = []
	end
	
	def setTeam(team)
		@team = team
	end
	
	def setHero(hero)
		@hero = hero
	end

	def addKill(killinfo)
		@kills.push(killinfo)
	end

	def addDeath(deathinfo)
		@deaths.push(deathinfo)
	end

	def addAssist(assistinfo)
		@assists.push(assistinfo)
	end

	def addGold(amt)
		@gold += amt
	end

	def addSnap(snap)
		@goldsnapshots << snap
	end

	def gpmAt(time)
		return @goldsnapshots[0..(time+10)/20].reduce(0){|sum, snap| sum+=snap.instance_variable_get(:@goldtotal)}
	end

	def to_s
		return "(#{@nickname}, #{@hero}, #{@team}, k:#{@kills.size}, d:#{@deaths.size}, a:#{@assists.size})\n"
	end
end

class PlayerGoldSnapshot
	def initialize(player, timeslot)
		@player = player
		@timeslot = timeslot
		@gold = []
		@goldlost = []
		@goldtotal = if timeslot == 0 then 10 else 20 end #need to also take into account the mode of the game
	end

	def addGold(amt, x, y, z, src)
		@gold << {amt: amt, x: x, y: y, z: z, src: src}
		@goldtotal += amt
	end

	def subtractGold(amt, x, y, z, src)
		@goldlost << {amt: amt, x: x, y: y, z: z, src: src}
		@goldtotal -= amt
	end

	def to_s
		return "Snap for #{@player} at time #{@timeslot * 20}s and #{@goldtotal}g. Sources were: #{@gold}"
	end
end

def parseParams(raw)
	params = {}
	raw.each do |param|
		params[param.split(":")[0].intern]=param.split(":")[1].tr("\"", "").strip
	end

	return params
end

#this works no matter what, but the list of deaths (below) will not be updated properly if people die before the game starts

def parseDeath(params, playerlist, goldsnapshots, logfile, deathinfo=nil)
	deathinfo = {} unless deathinfo
	deathinfo[:time] = params[:time]
	if params[:owner] then deathinfo[:killer] = params[:owner] else deathinfo[:killer] = params[:attacker] end
	deathinfo[:killed] = params[:player]
	deathinfo[:killingteam] = (3 - params[:team].to_i).to_s
	deathinfo[:x] = params[:x]
	deathinfo[:y] = params[:y]
	deathinfo[:z] = params[:z]
	pieces = logfile.readline.scan(/(?:"(?:\\.|[^"])*"|[^" ])+/)
	params = parseParams(pieces[1..-1]) if pieces.size>=1 
	#deathinfo[:assists] = params[:assists].split(",").reduce({}) {|p, n| p.store(n, [])} if params[:assists]
	# deathinfo[:assists] = []
	# params[:assists].split(",").each {|a| deathinfo[:assists].push(a)} if params[:assists]
	deathinfo[:assists] = params[:assists].split(',') if params[:assists]
	# puts params[:assists]
	# puts deathinfo[:assists]
	deathinfo[:expearned] = {}
	deathinfo[:goldearned] = {} unless deathinfo[:goldearned]
	while pieces[0] != "GOLD_LOST"
		line = logfile.readline
		pieces = line.scan(/(?:"(?:\\.|[^"])*"|[^" ])+/)
		params = parseParams(pieces[1..-1]) if pieces.size>=1 
		if pieces[0] == "EXP_EARNED"
			deathinfo[:expearned][params[:player]] = params[:experience]
		elsif pieces[0] == "GOLD_EARNED"
			deathinfo[:goldearned][params[:player]]? deathinfo[:goldearned][params[:player]] = (deathinfo[:goldearned][params[:player]].to_i + params[:gold].to_i).to_s : deathinfo[:goldearned][params[:player]] = params[:gold]
			goldsnapshots[(params[:time].to_i + 10050)/20000][params[:player]].addGold(params[:gold].to_i, params[:x], params[:y], params[:z], params[:source])
		elsif pieces[0] == "DAMAGE"
			deathinfo[:damage] = {} unless deathinfo[:damage]
			deathinfo[:damage][params[:player]] = {} unless deathinfo[:damage][params[:player]]
			deathinfo[:damage][params[:player]][params[:inflictor]] = params[:damage]
		elsif pieces[0] == "AWARD_FIRST_BLOOD"
			deathinfo[:goldearned][params[:player]] = (deathinfo[:goldearned][params[:player]].to_i + params[:gold].to_i).to_s
			goldsnapshots[(params[:time].to_i + 10050)/20000][params[:player]].addGold(params[:gold].to_i, params[:x], params[:y], params[:z], "BLOODLUST")
		elsif pieces[0] == "GOLD_LOST"
			deathinfo[:goldlost] = params[:gold]
			goldsnapshots[(params[:time].to_i + 10050)/20000][params[:player]].subtractGold(params[:gold].to_i, params[:x], params[:y], params[:z], params[:source])
		end
	end
	playerlist[deathinfo[:killer]].addKill(deathinfo)
	playerlist[deathinfo[:killed]].addDeath(deathinfo)
	deathinfo[:assists].each {|assist| playerlist[assist].addAssist(deathinfo)} if deathinfo[:assists]
	return deathinfo
end

def parseAssist(playerlist, goldsnapshots, logfile)
	deathinfo = {}
	pieces = logfile.readline.scan(/(?:"(?:\\.|[^"])*"|[^" ])+/)
	params = parseParams(pieces[1..-1])
	while pieces[0] == "GOLD_EARNED"
		deathinfo[:goldearned] = {}
		deathinfo[:goldearned][params[:player]] = params[:gold]
		goldsnapshots[(params[:time].to_i + 10050)/20000][params[:player]].addGold(params[:gold].to_i, params[:x], params[:y], params[:z], params[:source])
		pieces = logfile.readline.scan(/(?:"(?:\\.|[^"])*"|[^" ])+/)
		params = parseParams(pieces[1..-1])
	end
	return parseDeath(params, playerlist, goldsnapshots, logfile, deathinfo)
end

# this needs to be redone. seeing PLAYER_ACTIONS signifies that the next time block has started, so here we need to be creating the boxes for the next timeslot
def makeGoldSnaps(slot, playerlist, logfile)
	snaps = {} 
	playerlist.each_key do |player|
		newSnap = PlayerGoldSnapshot.new(player, slot)
		playerlist[player].addSnap(newSnap)
		snaps[player] = newSnap
	end
	return snaps
end

fname = "m124046983.log"
logfile = File.open(fname, "r")

matchdatetime = ""
server = ""
gameversion = ""
match = ""
settings = ""
kills = {} 	#this won't be updated properly if people die before the game starts
buffer = []
playerlist = {}
goldsnapshots = [] << {}

logfile.each do |line|
	buffer.push(line)
	buffer.delete_at(0) if buffer.size >= 20
	pieces = line.scan(/(?:"(?:\\.|[^"])*"|[^" ])+/)
	params = parseParams(pieces[1..-1]) if pieces.size>=1 
	if pieces[0] == "PLAYER_CONNECT"
		playerlist[params[:player]] = Hero.new(params[:player], params[:name])
		initSnap = PlayerGoldSnapshot.new(params[:player], 0)
		goldsnapshots[0][params[:player]] = initSnap
		playerlist[params[:player]].addSnap(initSnap)
		#add goldsnapshots
	elsif pieces[0] == "PLAYER_TEAM_CHANGE"
		playerlist[params[:player]].setTeam(params[:team])
	elsif pieces[0] == "PLAYER_SELECT"
		playerlist[params[:player]].setHero(params[:hero])
	elsif pieces[0] == "PLAYER_CHAT"
		#puts "#{playerlist[params[:player]]} [#{params[:target].upcase}]: #{params[:msg]}"
	elsif pieces[0] == "HERO_DEATH"
		kills[params[:time]] = parseDeath(params, playerlist, goldsnapshots, logfile, nil)
	elsif pieces[0] == "KILL"
		#puts "Kill"
	elsif pieces[0] == "HERO_ASSIST"
		kills[params[:time]] = parseAssist(playerlist, goldsnapshots, logfile)
	elsif pieces[0] == "PLAYER_ACTIONS"
		if params[:time]
			slot = (params[:time].to_i+10050)/20000
			goldsnapshots << makeGoldSnaps(slot, playerlist, logfile)
		end
		logfile.lineno = $. + 9 #make sure this works
	elsif pieces[0] == "GOLD_EARNED"
		slot = (params[:time].to_i+10050)/20000
		goldsnapshots[slot][params[:player]].addGold(params[:gold].to_i, params[:x], params[:y], params[:z], params[:source])
		playerlist[params[:player]].addGold(params[:gold].to_i)
	end

end

puts playerlist["0"].gpmAt(10)

