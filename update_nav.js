const fs = require('fs');
const path = require('path');

const htmlDir = path.join(__dirname, 'src', 'main', 'resources', 'static');

const activeCls = 'text-primary border-b-2 border-primary pb-1';
const inactiveCls = 'text-slate-500 hover:text-primary transition-colors';

const headerTemplate = `<header class="bg-white border-b border-slate-200 sticky top-0 z-50">
  <div class="flex justify-between items-center w-full px-8 py-4 max-w-full">
    <div class="flex items-center gap-8">
      <span class="text-xl font-bold text-primary font-headline tracking-tight">Scholar Hub Admin</span>
      <nav class="hidden md:flex items-center gap-6 font-['Manrope'] font-semibold tracking-tight">
        <a class="{dashboard_cls}" href="admin-dashboard.html">Dashboard</a>
        <a class="{user_cls}" href="admin-user.html">Users</a>
        <a class="{events_cls}" href="admin-events.html">Events</a>
        <a class="{clubs_cls}" href="admin-clubs.html">Clubs</a>
        <a class="{venue_cls}" href="admin-venue.html">Venues</a>
        <a class="{feedback_cls}" href="admin-feedback.html">Feedback</a>
      </nav>
    </div>
    <div class="flex items-center gap-4">
      <div class="relative hidden lg:block">
        <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">search</span>
        <input class="bg-slate-50 border border-slate-200 rounded-lg pl-10 pr-4 py-2 text-sm w-64 focus:ring-primary focus:border-primary transition-all" placeholder="Search resources..." type="text"/>
      </div>
      <button class="p-2 text-slate-500 hover:bg-slate-100 rounded-lg relative">
        <span class="material-symbols-outlined">notifications</span>
        <span class="absolute top-2 right-2 w-2 h-2 bg-error rounded-full"></span>
      </button>
      <button class="p-2 text-slate-500 hover:bg-slate-100 rounded-lg">
        <span class="material-symbols-outlined">settings</span>
      </button>
      <div class="flex items-center gap-2 pl-2 border-l border-slate-200">
        <div class="h-9 w-9 rounded-full overflow-hidden border border-slate-200">
          <img alt="Admin Profile" src="https://lh3.googleusercontent.com/aida-public/AB6AXuD1VU3S62RpOxsV3h8nGrcYROkGk0AVy0eLafOso_aDg2Cg9mPuYLDbwSh1ewpN7tFYpX7QHye3PKJcESNfg0--IIZkQwIhmUyC1fQHd9NudN1czi0AHY2ICdKjzGeo1UoLk4KCvmePPST3q15dmRVW0_E3c8bDNDrcFW0gwJN4zQMR-OM5gRHXic0kp3Xc3TWWkf32vSapgqjhOFjN_l1FoPmfdNr5HTalimgqA72vA4kiMZfmhvhvqLmXLTTvQLuGzkjghfHfgwcA"/>
        </div>
        <span class="text-sm font-semibold text-slate-700 hidden lg:block">Admin User</span>
      </div>
    </div>
  </div>
</header>`;

function getActiveTab(filename) {
    if (filename.includes("club")) return "clubs";
    if (filename.includes("venue")) return "venue";
    if (filename.includes("event")) return "events";
    if (filename.includes("user")) return "user";
    if (filename.includes("feedback")) return "feedback";
    return "dashboard";
}

const headerRegex = /<header[\s\S]*?<\/header>/;

fs.readdirSync(htmlDir).forEach(file => {
    if (file.startsWith("admin-") && file.endsWith(".html")) {
        const filepath = path.join(htmlDir, file);
        const content = fs.readFileSync(filepath, 'utf8');
        
        const tab = getActiveTab(file);
        
        const rep = {
            dashboard_cls: tab === "dashboard" ? activeCls : inactiveCls,
            user_cls: tab === "user" ? activeCls : inactiveCls,
            events_cls: tab === "events" ? activeCls : inactiveCls,
            clubs_cls: tab === "clubs" ? activeCls : inactiveCls,
            venue_cls: tab === "venue" ? activeCls : inactiveCls,
            feedback_cls: tab === "feedback" ? activeCls : inactiveCls,
        };
        
        let newHeader = headerTemplate;
        for (const [key, val] of Object.entries(rep)) {
            newHeader = newHeader.replace(`{${key}}`, val);
        }
        
        const newContent = content.replace(headerRegex, newHeader);
        
        if (content !== newContent) {
            fs.writeFileSync(filepath, newContent, 'utf8');
            console.log(`Updated ${file}`);
        } else {
            console.log(`Skipped ${file}`);
        }
    }
});
