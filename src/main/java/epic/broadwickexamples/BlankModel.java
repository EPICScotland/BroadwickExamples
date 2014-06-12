
package epic.broadwickexamples;

import broadwick.model.Model;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BlankModel extends Model {

    @Override
    public final void init() {
	log.info("Initialising project");
    }

    @Override
    public final void run() {
	log.info("Running project");
    }

    @Override
    public final void finalise() {
	log.info("Closing project");
    }

}

