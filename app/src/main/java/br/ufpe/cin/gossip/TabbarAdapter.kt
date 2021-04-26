import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.ufpe.cin.gossip.ConversasFragment
import br.ufpe.cin.gossip.SalasFragment
import br.ufpe.cin.gossip.VizinhosFragment

@Suppress("DEPRECATION")
internal class TabbarAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                SalasFragment()
            }
            1 -> {
                ConversasFragment()
            }
            2 -> {
                VizinhosFragment()
            }
            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}